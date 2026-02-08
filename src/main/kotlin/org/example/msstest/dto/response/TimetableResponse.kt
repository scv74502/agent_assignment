package org.example.msstest.dto.response

import org.example.msstest.domain.entity.CourseSchedule
import java.time.DayOfWeek
import java.time.LocalTime

data class TimetableResponse(
    val studentId: Long,
    val totalCredits: Int,
    val entries: List<TimetableEntry>,
)

data class TimetableEntry(
    val courseId: Long,
    val courseCode: String,
    val courseName: String,
    val professorName: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val location: String,
) {
    companion object {
        fun from(schedule: CourseSchedule): TimetableEntry =
            TimetableEntry(
                courseId = schedule.course.id,
                courseCode = schedule.course.courseCode.value,
                courseName = schedule.course.courseName,
                professorName = schedule.course.professor.name,
                dayOfWeek = schedule.dayOfWeek,
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                location = schedule.location,
            )
    }
}
