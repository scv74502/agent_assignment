package org.example.msstest.common.initializer

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.initializer")
data class InitializerProperties(
    val enabled: Boolean = false,
    val departments: Int = 12,
    val professors: Int = 100,
    val courses: Int = 500,
    val students: Int = 10000,
    val batchSize: Int = 1000,
)
