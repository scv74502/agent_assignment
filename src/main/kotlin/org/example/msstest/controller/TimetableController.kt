package org.example.msstest.controller

import org.example.msstest.controller.openapi.TimetableApi
import org.example.msstest.dto.response.TimetableResponse
import org.example.msstest.service.TimetableService
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
