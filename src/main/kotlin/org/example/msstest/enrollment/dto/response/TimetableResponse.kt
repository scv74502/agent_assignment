package org.example.msstest.enrollment.dto.response

import java.time.DayOfWeek
import java.time.LocalTime

data class TimetableResponse(
    val studentId: Long,
    val totalCredits: Int,
    val entries: List<TimetableEntry>,
)
