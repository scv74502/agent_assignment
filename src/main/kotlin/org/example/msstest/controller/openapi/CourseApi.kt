package org.example.msstest.controller.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.msstest.dto.response.CourseResponse
import org.example.msstest.dto.response.ErrorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Course", description = "강좌 API")
@RequestMapping("/api/v1/courses")
interface CourseApi {
    @Operation(summary = "전체 강좌 조회", description = "모든 강좌 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = CourseResponse::class))],
    )
    @GetMapping
    fun getAllCourses(): ResponseEntity<List<CourseResponse>>

    @Operation(summary = "수강 가능 강좌 조회", description = "정원이 남아있는 강좌 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = CourseResponse::class))],
    )
    @GetMapping("/available")
    fun getAvailableCourses(): ResponseEntity<List<CourseResponse>>

    @Operation(summary = "강좌 상세 조회", description = "특정 강좌의 상세 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = CourseResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "강좌를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{courseId}")
    fun getCourseById(
        @Parameter(description = "강좌 ID") @PathVariable courseId: Long,
    ): ResponseEntity<CourseResponse>

    @Operation(summary = "교수별 강좌 조회", description = "특정 교수가 담당하는 강좌 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = CourseResponse::class))],
    )
    @GetMapping(params = ["professorId"])
    fun getCoursesByProfessor(
        @Parameter(description = "교수 ID") @RequestParam professorId: Long,
    ): ResponseEntity<List<CourseResponse>>
}
