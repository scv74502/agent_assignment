package org.example.msstest.dto.response

import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.CourseSchedule
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class CourseResponse(
    val id: Long,
    val courseCode: String,
    val courseName: String,
    val credits: Int,
    val capacity: Int,
    val currentEnrollment: Int,
    val remainingCapacity: Int,
    val professorId: Long,
    val professorName: String,
    val schedule: String,
    val schedules: List<ScheduleResponse>,
) {
    companion object {
        private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val dayOrder: List<DayOfWeek> =
            listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY,
                DayOfWeek.SUNDAY,
            )
        private val dayLabels: Map<DayOfWeek, String> =
            mapOf(
                DayOfWeek.MONDAY to "월",
                DayOfWeek.TUESDAY to "화",
                DayOfWeek.WEDNESDAY to "수",
                DayOfWeek.THURSDAY to "목",
                DayOfWeek.FRIDAY to "금",
                DayOfWeek.SATURDAY to "토",
                DayOfWeek.SUNDAY to "일",
            )

        fun from(course: Course): CourseResponse =
            CourseResponse(
                id = course.id,
                courseCode = course.courseCode.value,
                courseName = course.courseName,
                credits = course.credits.value,
                capacity = course.capacity,
                currentEnrollment = course.currentEnrollment,
                remainingCapacity = course.remainingCapacity,
                professorId = course.professor.id,
                professorName = course.professor.name,
                schedule = formatSchedules(course.schedules),
                schedules = course.schedules.map { ScheduleResponse.from(it) },
            )

        private fun formatSchedules(schedules: List<CourseSchedule>): String {
            if (schedules.isEmpty()) return ""
            val orderIndex = dayOrder.withIndex().associate { it.value to it.index }
            val sorted =
                schedules.sortedWith(
                    compareBy<CourseSchedule> { orderIndex[it.dayOfWeek] ?: Int.MAX_VALUE }
                        .thenBy { it.startTime }
                        .thenBy { it.endTime },
                )

            val result = StringBuilder()
            var currentDay: DayOfWeek? = null

            for (schedule in sorted) {
                val day = schedule.dayOfWeek
                val timeRange =
                    "${schedule.startTime.format(timeFormatter)}~${schedule.endTime.format(timeFormatter)}"

                if (currentDay == null || currentDay != day) {
                    if (result.isNotEmpty()) result.append(", ")
                    result.append(dayLabels[day] ?: day.name)
                    result.append(" ")
                    result.append(timeRange)
                    currentDay = day
                } else {
                    result.append(", ")
                    result.append(timeRange)
                }
            }
            return result.toString()
        }
    }
}

data class ScheduleResponse(
    val id: Long,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val location: String,
) {
    companion object {
        fun from(schedule: CourseSchedule): ScheduleResponse =
            ScheduleResponse(
                id = schedule.id,
                dayOfWeek = schedule.dayOfWeek,
                startTime = schedule.startTime,
                endTime = schedule.endTime,
                location = schedule.location,
            )
    }
}
