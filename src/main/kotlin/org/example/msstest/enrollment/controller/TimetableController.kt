package org.example.msstest.enrollment.controller

import org.example.msstest.enrollment.dto.response.TimetableResponse
import org.example.msstest.enrollment.openapi.TimetableApi
import org.example.msstest.enrollment.service.TimetableService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TimetableController(
    private val timetableService: TimetableService,
) : TimetableApi {
    override fun getTimetable(studentId: Long): ResponseEntity<TimetableResponse> {
        val timetable = timetableService.getTimetable(studentId)
        return ResponseEntity.ok(timetable)
    }
}
