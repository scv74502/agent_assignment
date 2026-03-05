package org.example.msstest.common.healthcheck

import org.example.msstest.common.healthcheck.openapi.HealthCheckApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController : HealthCheckApi {
    override fun health(): ResponseEntity<Void> = ResponseEntity.ok().build()
}
