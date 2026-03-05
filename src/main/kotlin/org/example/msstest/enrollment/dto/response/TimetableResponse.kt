package org.example.msstest.enrollment.dto.response

data class TimetableResponse(
    val studentId: Long,
    val totalCredits: Int,
    val entries: List<TimetableEntry>,
)
