package org.example.msstest.enrollment.service

import org.example.msstest.course.repository.CourseScheduleRepository
import org.example.msstest.enrollment.dto.response.TimetableEntry
import org.example.msstest.enrollment.dto.response.TimetableResponse
import org.example.msstest.enrollment.repository.EnrollmentRepository
import org.example.msstest.student.exception.StudentException
import org.example.msstest.student.service.StudentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimetableService(
    private val studentService: StudentService,
    private val enrollmentRepository: EnrollmentRepository,
    private val courseScheduleRepository: CourseScheduleRepository,
) {
    @Transactional(readOnly = true)
    fun getTimetable(studentId: Long): TimetableResponse {
        if (!studentService.existsById(studentId)) {
            throw StudentException.NotFound(studentId)
        }

        val totalCredits = enrollmentRepository.sumCreditsByStudentId(studentId)
        val schedules = courseScheduleRepository.findByStudentEnrollments(studentId)

        val entries =
            schedules
                .map { TimetableEntry.from(it) }
                .sortedWith(compareBy({ it.dayOfWeek }, { it.startTime }))

        return TimetableResponse(
            studentId = studentId,
            totalCredits = totalCredits,
            entries = entries,
        )
    }
}
