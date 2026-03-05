package org.example.msstest.service

import org.example.msstest.domain.entity.CourseSchedule
import org.example.msstest.domain.entity.Enrollment
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.dto.response.EnrollmentResponse
import org.example.msstest.exception.CourseException
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.common.exception.LockException
import org.example.msstest.common.lock.RedisLockService
import org.example.msstest.common.queue.EnrollmentQueueService
import org.example.msstest.repository.CourseRepository
import org.example.msstest.repository.CourseScheduleRepository
import org.example.msstest.repository.EnrollmentRepository
import org.example.msstest.student.exception.StudentException
import org.example.msstest.student.service.StudentService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val studentService: StudentService,
    private val courseRepository: CourseRepository,
    private val courseScheduleRepository: CourseScheduleRepository,
    private val redisLockService: RedisLockService,
    private val queueService: EnrollmentQueueService,
    private val transactionTemplate: TransactionTemplate,
    @Value("\${enrollment.max-credits:18}") private val maxCredits: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enroll(
        studentId: Long,
        courseId: Long,
    ): EnrollmentResponse {
        if (!studentService.existsById(studentId)) {
            throw StudentException.NotFound(studentId)
        }

        if (!courseRepository.existsById(courseId)) {
            throw CourseException.NotFound(courseId)
        }

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED)) {
            throw EnrollmentException.AlreadyEnrolled(studentId, courseId)
        }

        val lockKey = RedisLockService.enrollmentLockKey(studentId)
        val result =
            redisLockService.executeWithLock(lockKey) {
                transactionTemplate.execute {
                    val student = studentService.findStudentEntityById(studentId)

                    if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                            studentId,
                            courseId,
                            EnrollmentStatus.ENROLLED,
                        )
                    ) {
                        throw EnrollmentException.AlreadyEnrolled(studentId, courseId)
                    }

                    val course =
                        courseRepository
                            .findByIdWithLock(courseId)
                            .orElseThrow { CourseException.NotFound(courseId) }

                    validateCreditLimit(studentId, course.credits.value)
                    validateScheduleConflict(studentId, courseId)

                    if (course.isFull()) {
                        throw CourseException.Full(courseId)
                    }

                    course.incrementEnrollment()
                    courseRepository.save(course)

                    val enrollment = Enrollment.create(student, course)
                    EnrollmentResponse.from(enrollmentRepository.save(enrollment))
                }
            }

        return result
            ?: throw LockException.AcquisitionFailed("student:$studentId")
    }

    fun cancel(
        studentId: Long,
        courseId: Long,
    ): EnrollmentResponse {
        val enrollment =
            enrollmentRepository
                .findByStudentIdAndCourseIdAndStatus(
                    studentId,
                    courseId,
                    EnrollmentStatus.ENROLLED,
                ).orElseThrow { EnrollmentException.NotFound(0) }

        val lockKey = RedisLockService.enrollmentLockKey(studentId)
        val result =
            redisLockService.executeWithLock(lockKey) {
                transactionTemplate.execute {
                    val managedEnrollment =
                        enrollmentRepository
                            .findById(enrollment.id)
                            .orElseThrow { EnrollmentException.NotFound(enrollment.id) }
                    managedEnrollment.cancel()

                    val course =
                        courseRepository
                            .findByIdWithLock(courseId)
                            .orElseThrow { CourseException.NotFound(courseId) }
                    course.decrementEnrollment()
                    courseRepository.save(course)

                    EnrollmentResponse.from(enrollmentRepository.save(managedEnrollment))
                }
            }

        return result
            ?: throw LockException.AcquisitionFailed("student:$studentId")
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    fun getEnrollmentsByStudent(studentId: Long): List<EnrollmentResponse> {
        if (!studentService.existsById(studentId)) {
            throw StudentException.NotFound(studentId)
        }

        return enrollmentRepository
            .findByStudentIdAndStatus(studentId, EnrollmentStatus.ENROLLED)
            .map { EnrollmentResponse.from(it) }
    }

    private fun validateCreditLimit(
        studentId: Long,
        requestedCredits: Int,
    ) {
        val currentCredits = enrollmentRepository.sumCreditsByStudentId(studentId)
        if (currentCredits + requestedCredits > maxCredits) {
            throw EnrollmentException.CreditLimitExceeded(currentCredits, requestedCredits, maxCredits)
        }
    }

    private fun validateScheduleConflict(
        studentId: Long,
        courseId: Long,
    ) {
        val enrolledSchedules = courseScheduleRepository.findByStudentEnrollments(studentId)
        val newSchedules = courseScheduleRepository.findByCourseId(courseId)

        for (enrolled in enrolledSchedules) {
            for (newSchedule in newSchedules) {
                if (hasTimeOverlap(enrolled, newSchedule)) {
                    throw EnrollmentException.ScheduleConflict(enrolled.course.id, courseId)
                }
            }
        }
    }

    private fun hasTimeOverlap(
        schedule1: CourseSchedule,
        schedule2: CourseSchedule,
    ): Boolean {
        if (schedule1.dayOfWeek != schedule2.dayOfWeek) return false
        return schedule1.startTime < schedule2.endTime && schedule2.startTime < schedule1.endTime
    }
}
