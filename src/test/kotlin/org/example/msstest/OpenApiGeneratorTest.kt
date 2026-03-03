package org.example.msstest

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.nio.file.Files
import java.nio.file.Paths

@AutoConfigureMockMvc
class OpenApiGeneratorTest : IntegrationTestBase() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun generateOpenApiDocs() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val jsonContent = result.response.contentAsString
        val formattedJson = ObjectMapper().readTree(jsonContent).toPrettyString()

        val outputDir = Paths.get("docs")
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir)
        }

        val outputFile = outputDir.resolve("openapi.json").toFile()
        outputFile.writeText(formattedJson)

        println("OpenAPI documentation generated at: ${outputFile.absolutePath}")
    }
}
