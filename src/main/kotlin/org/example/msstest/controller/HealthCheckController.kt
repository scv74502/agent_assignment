package org.example.msstest.controller

import org.example.msstest.controller.openapi.HealthCheckApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController : HealthCheckApi {
    override fun health(): ResponseEntity<Void> = ResponseEntity.ok().build()
}
