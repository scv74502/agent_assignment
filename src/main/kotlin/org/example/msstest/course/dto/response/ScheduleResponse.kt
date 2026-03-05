package org.example.msstest.course.dto.response

import org.example.msstest.course.entity.CourseSchedule
import java.time.DayOfWeek
import java.time.LocalTime

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
