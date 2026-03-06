package org.example.msstest.enrollment.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.msstest.common.dto.ErrorResponse
import org.example.msstest.enrollment.constants.EnrollmentConstants
import org.example.msstest.enrollment.dto.response.TimetableResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = EnrollmentConstants.TIMETABLE_TAG_NAME, description = EnrollmentConstants.TIMETABLE_TAG_DESCRIPTION)
@RequestMapping(EnrollmentConstants.TIMETABLE_BASE_PATH)
interface TimetableApi {
    @Operation(summary = "학생 시간표 조회", description = "특정 학생의 시간표를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = TimetableResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "학생을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping(EnrollmentConstants.PATH_STUDENTS_BY_ID)
    fun getTimetable(
        @Parameter(description = "학생 ID") @PathVariable studentId: Long,
    ): ResponseEntity<TimetableResponse>
}
