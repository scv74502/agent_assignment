package org.example.msstest.service

import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.CourseSchedule
import org.example.msstest.domain.entity.Enrollment
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.dto.response.EnrollmentResponse
import org.example.msstest.exception.CourseException
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.exception.LockException
import org.example.msstest.exception.StudentException
import org.example.msstest.lock.RedisLockService
import org.example.msstest.queue.EnrollmentQueueService
import org.example.msstest.repository.CourseRepository
import org.example.msstest.repository.CourseScheduleRepository
import org.example.msstest.repository.EnrollmentRepository
import org.example.msstest.repository.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EnrollmentService(
    private val enrollmentRepository: EnrollmentRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository,
    private val courseScheduleRepository: CourseScheduleRepository,
    private val redisLockService: RedisLockService,
    private val queueService: EnrollmentQueueService,
    @Value("\${enrollment.max-credits:18}") private val maxCredits: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun enroll(
        studentId: Long,
        courseId: Long,
    ): EnrollmentResponse {
        val student =
            studentRepository
                .findById(studentId)
                .orElseThrow { StudentException.NotFound(studentId) }

        if (!courseRepository.existsById(courseId)) {
            throw CourseException.NotFound(courseId)
        }

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(studentId, courseId, EnrollmentStatus.ENROLLED)) {
            throw EnrollmentException.AlreadyEnrolled(studentId, courseId)
        }

        val lockKey = RedisLockService.enrollmentLockKey(courseId)
        val result =
            redisLockService.executeWithLock(lockKey) {
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
                enrollmentRepository.save(enrollment)
            }

        return result?.let { EnrollmentResponse.from(it) }
            ?: throw LockException.AcquisitionFailed("course:$courseId")
    }

    @Transactional
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

        val lockKey = RedisLockService.enrollmentLockKey(courseId)
        val result =
            redisLockService.executeWithLock(lockKey) {
                enrollment.cancel()

                val course =
                    courseRepository
                        .findByIdWithLock(courseId)
                        .orElseThrow { CourseException.NotFound(courseId) }
                course.decrementEnrollment()
                courseRepository.save(course)

                enrollmentRepository.save(enrollment)
            }

        return result?.let { EnrollmentResponse.from(it) }
            ?: throw LockException.AcquisitionFailed("course:$courseId")
    }

    @Transactional(readOnly = true)
    fun getEnrollmentsByStudent(studentId: Long): List<EnrollmentResponse> {
        if (!studentRepository.existsById(studentId)) {
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
