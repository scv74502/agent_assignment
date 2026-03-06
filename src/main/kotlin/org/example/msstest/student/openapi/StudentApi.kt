package org.example.msstest.student.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.msstest.common.dto.ErrorResponse
import org.example.msstest.student.constants.StudentConstants
import org.example.msstest.student.dto.response.StudentResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = StudentConstants.TAG_NAME, description = StudentConstants.TAG_DESCRIPTION)
@RequestMapping(StudentConstants.BASE_PATH)
interface StudentApi {
    @Operation(summary = "전체 학생 조회", description = "모든 학생 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = StudentResponse::class))],
    )
    @GetMapping
    fun getAllStudents(): ResponseEntity<List<StudentResponse>>

    @Operation(summary = "학생 상세 조회 (ID)", description = "ID로 특정 학생의 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = StudentResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "학생을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping(StudentConstants.PATH_STUDENT_ID)
    fun getStudentById(
        @Parameter(description = "학생 ID") @PathVariable studentId: Long,
    ): ResponseEntity<StudentResponse>

    @Operation(summary = "학생 상세 조회 (학번)", description = "학번으로 특정 학생의 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = StudentResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "학생을 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping(params = ["studentNo"])
    fun getStudentByStudentNo(
        @Parameter(description = "학번") @RequestParam studentNo: String,
    ): ResponseEntity<StudentResponse>
}
