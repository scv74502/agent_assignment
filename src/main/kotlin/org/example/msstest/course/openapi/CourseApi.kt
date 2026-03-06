package org.example.msstest.course.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.msstest.common.dto.CursorPageResponse
import org.example.msstest.common.dto.ErrorResponse
import org.example.msstest.course.constants.CourseConstants
import org.example.msstest.course.dto.response.CourseResponse
import org.example.msstest.course.entity.CourseType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = CourseConstants.TAG_NAME, description = CourseConstants.TAG_DESCRIPTION)
@RequestMapping(CourseConstants.BASE_PATH)
interface CourseApi {
    @Operation(summary = "전체 강좌 조회", description = "커서 기반 페이지네이션으로 강좌 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
    )
    @GetMapping
    fun getAllCourses(
        @Parameter(description = "커서 (이전 페이지의 마지막 강좌 ID)")
        @RequestParam(required = false) cursor: Long?,
        @Parameter(description = "페이지 크기 (기본값: 20, 최대: 100)")
        @RequestParam(required = false) size: Int?,
        @Parameter(description = "학과 필터")
        @RequestParam(required = false) department: String?,
        @Parameter(description = "이수구분 필터 (MAJOR_REQUIRED, MAJOR_ELECTIVE, GENERAL_REQUIRED, GENERAL_ELECTIVE)")
        @RequestParam(required = false) courseType: CourseType?,
    ): ResponseEntity<CursorPageResponse<CourseResponse>>

    @Operation(summary = "수강 가능 강좌 조회", description = "정원이 남아있는 강좌를 커서 기반 페이지네이션으로 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
    )
    @GetMapping(CourseConstants.PATH_AVAILABLE)
    fun getAvailableCourses(
        @Parameter(description = "커서 (이전 페이지의 마지막 강좌 ID)")
        @RequestParam(required = false) cursor: Long?,
        @Parameter(description = "페이지 크기 (기본값: 20, 최대: 100)")
        @RequestParam(required = false) size: Int?,
        @Parameter(description = "학과 필터")
        @RequestParam(required = false) department: String?,
        @Parameter(description = "이수구분 필터 (MAJOR_REQUIRED, MAJOR_ELECTIVE, GENERAL_REQUIRED, GENERAL_ELECTIVE)")
        @RequestParam(required = false) courseType: CourseType?,
    ): ResponseEntity<CursorPageResponse<CourseResponse>>

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
    @GetMapping(CourseConstants.PATH_COURSE_ID)
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
