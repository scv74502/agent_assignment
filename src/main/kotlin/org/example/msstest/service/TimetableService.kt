package org.example.msstest.service

import org.example.msstest.dto.response.TimetableEntry
import org.example.msstest.dto.response.TimetableResponse
import org.example.msstest.exception.StudentException
import org.example.msstest.repository.CourseScheduleRepository
import org.example.msstest.repository.EnrollmentRepository
import org.example.msstest.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimetableService(
    private val studentRepository: StudentRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val courseScheduleRepository: CourseScheduleRepository,
) {
    @Transactional(readOnly = true)
    fun getTimetable(studentId: Long): TimetableResponse {
        if (!studentRepository.existsById(studentId)) {
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
