HealthCheckApi를 먼저 swagger로 구성하고 HealthCheckAPI 구현해. 
# 구현 예시
```
GET /health
응답: HTTP 200 OK
```

```kotlin
 @Operation(
        summary = "헬스체크",
        description = "api 구동 여부 체크하는 기본 엔드포인트",
    )
    @ApiResponse(
        responseCode = "200",
        description = "서버가 성공적으로 구동되었습니다.",
    )
```