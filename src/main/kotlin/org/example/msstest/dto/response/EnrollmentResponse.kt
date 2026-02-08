package org.example.msstest.dto.response

import org.example.msstest.domain.entity.Enrollment
import org.example.msstest.domain.entity.EnrollmentStatus
import java.time.LocalDateTime

data class EnrollmentResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val courseId: Long,
    val courseName: String,
    val courseCode: String,
    val credits: Int,
    val professorName: String,
    val status: EnrollmentStatus,
    val enrolledAt: LocalDateTime,
) {
    companion object {
        fun from(enrollment: Enrollment): EnrollmentResponse =
            EnrollmentResponse(
                id = enrollment.id,
                studentId = enrollment.student.id,
                studentName = enrollment.student.name,
                courseId = enrollment.course.id,
                courseName = enrollment.course.courseName,
                courseCode = enrollment.course.courseCode.value,
                credits = enrollment.course.credits.value,
                professorName = enrollment.course.professor.name,
                status = enrollment.status,
                enrolledAt = enrollment.createdAt,
            )
    }
}
