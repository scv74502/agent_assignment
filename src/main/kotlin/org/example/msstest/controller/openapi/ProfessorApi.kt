package org.example.msstest.controller.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.msstest.common.dto.ErrorResponse
import org.example.msstest.dto.response.ProfessorResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Professor", description = "교수 API")
@RequestMapping("/api/v1/professors")
interface ProfessorApi {
    @Operation(summary = "전체 교수 조회", description = "모든 교수 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = ProfessorResponse::class))],
    )
    @GetMapping
    fun getAllProfessors(): ResponseEntity<List<ProfessorResponse>>

    @Operation(summary = "교수 상세 조회", description = "특정 교수의 정보를 조회합니다")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = ProfessorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "교수를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @GetMapping("/{professorId}")
    fun getProfessorById(
        @Parameter(description = "교수 ID") @PathVariable professorId: Long,
    ): ResponseEntity<ProfessorResponse>

    @Operation(summary = "학과별 교수 조회", description = "특정 학과의 교수 목록을 조회합니다")
    @ApiResponse(
        responseCode = "200",
        description = "조회 성공",
        content = [Content(schema = Schema(implementation = ProfessorResponse::class))],
    )
    @GetMapping(params = ["department"])
    fun getProfessorsByDepartment(
        @Parameter(description = "학과명") @RequestParam department: String,
    ): ResponseEntity<List<ProfessorResponse>>
}
