package org.example.msstest.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    @Value("\${swagger.server-url:}") private val serverUrl: String,
    @Value("\${swagger.server-description:API Server}") private val serverDescription: String,
) {
    companion object {
        const val API_DOC_VERSION = "v1.0.0"
        const val API_DOC_TITLE = "Musinsa Test API"
        const val API_DOC_DESCRIPTION = "수강신청 API."
    }

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(swaggerInfo())
            .servers(initializeServers())

    private fun swaggerInfo(): Info =
        Info()
            .version(API_DOC_VERSION)
            .title(API_DOC_TITLE)
            .description(API_DOC_DESCRIPTION)

    private fun initializeServers(): List<Server> =
        if (serverUrl.isNotBlank()) {
            listOf(Server().url(serverUrl).description(serverDescription))
        } else {
            emptyList()
        }
}
