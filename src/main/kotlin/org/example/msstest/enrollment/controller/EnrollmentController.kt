package org.example.msstest.enrollment.controller

import org.example.msstest.enrollment.dto.request.CancelEnrollmentRequest
import org.example.msstest.enrollment.dto.request.EnrollmentRequest
import org.example.msstest.enrollment.dto.response.EnrollmentResponse
import org.example.msstest.enrollment.openapi.EnrollmentApi
import org.example.msstest.enrollment.service.EnrollmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class EnrollmentController(
    private val enrollmentService: EnrollmentService,
) : EnrollmentApi {
    override fun enroll(request: EnrollmentRequest): ResponseEntity<EnrollmentResponse> {
        val response = enrollmentService.enroll(request.studentId, request.courseId)
        return ResponseEntity.ok(response)
    }

    override fun cancel(request: CancelEnrollmentRequest): ResponseEntity<EnrollmentResponse> {
        val response = enrollmentService.cancel(request.studentId, request.courseId)
        return ResponseEntity.ok(response)
    }

    override fun getEnrollmentsByStudent(studentId: Long): ResponseEntity<List<EnrollmentResponse>> {
        val responses = enrollmentService.getEnrollmentsByStudent(studentId)
        return ResponseEntity.ok(responses)
    }
}
