package org.example.msstest.initializer.generator

import org.example.msstest.initializer.DataTokens
import java.time.DayOfWeek
import java.time.LocalTime
import kotlin.random.Random

data class ScheduleData(
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val location: String,
)

class ScheduleGenerator(
    private val random: Random = Random.Default,
) {
    private val weekdays = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
    )

    fun generateForCredits(credits: Int): List<ScheduleData> {
        val schedules = mutableListOf<ScheduleData>()
        val sessionsNeeded = when (credits) {
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 2
            5 -> 3
            6 -> 3
            else -> 2
        }

        val usedDays = mutableSetOf<DayOfWeek>()
        val building = DataTokens.BUILDINGS[random.nextInt(DataTokens.BUILDINGS.size)]
        val roomNumber = (100 + random.nextInt(400)).toString()
        val location = "$building $roomNumber"

        repeat(sessionsNeeded) {
            val availableDays = weekdays.filter { it !in usedDays }
            if (availableDays.isEmpty()) return@repeat

            val day = availableDays[random.nextInt(availableDays.size)]
            usedDays.add(day)

            val timeSlot = DataTokens.TIME_SLOTS[random.nextInt(DataTokens.TIME_SLOTS.size)]
            val startTime = LocalTime.of(timeSlot.startHour, timeSlot.startMinute)
            val endTime = startTime.plusMinutes(timeSlot.durationMinutes.toLong())

            schedules.add(
                ScheduleData(
                    dayOfWeek = day,
                    startTime = startTime,
                    endTime = endTime,
                    location = location,
                ),
            )
        }

        return schedules
    }
}
