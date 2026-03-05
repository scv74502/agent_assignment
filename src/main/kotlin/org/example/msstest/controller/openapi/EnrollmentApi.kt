package org.example.msstest.controller.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.msstest.dto.request.CancelEnrollmentRequest
import org.example.msstest.dto.request.EnrollmentRequest
import org.example.msstest.dto.response.EnrollmentResponse
import org.example.msstest.common.dto.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Enrollment", description = "수강신청 API")
@RequestMapping("/api/v1/enrollments")
interface EnrollmentApi {
    @Operation(summary = "수강신청", description = "학생이 강좌에 수강신청합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "수강신청 성공",
                content = [Content(schema = Schema(implementation = EnrollmentResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "최대 학점 초과",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "학생 또는 강좌를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "이미 수강신청함 / 정원 초과 / 시간표 중복",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @PostMapping
    fun enroll(
        @Valid @RequestBody request: EnrollmentRequest,
    ): ResponseEntity<EnrollmentResponse>

    @Operation(summary = "수강취소", description = "학생이 강좌 수강신청을 취소합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "수강취소 성공",
                content = [Content(schema = Schema(implementation = EnrollmentResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "수강신청 내역을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @DeleteMapping
    fun cancel(
        @Valid @RequestBody request: CancelEnrollmentRequest,
    ): ResponseEntity<EnrollmentResponse>

    @Operation(summary = "학생별 수강신청 내역 조회", description = "특정 학생의 수강신청 내역을 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = EnrollmentResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "학생을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/students/{studentId}")
    fun getEnrollmentsByStudent(
        @Parameter(description = "학생 ID") @PathVariable studentId: Long,
    ): ResponseEntity<List<EnrollmentResponse>>
}
