package org.example.msstest.controller.openapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping
interface HealthCheckApi {
    @Operation(
        summary = "헬스체크",
        description = "api 구동 여부 체크하는 기본 엔드포인트",
    )
    @ApiResponse(
        responseCode = "200",
        description = "서버가 성공적으로 구동되었습니다.",
    )
    @GetMapping("/health")
    fun health(): ResponseEntity<Void>
}
