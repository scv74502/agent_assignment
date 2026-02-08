package org.example.msstest.config

import com.redis.testcontainers.RedisContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestContainerConfig {
    companion object {
        private val mysqlContainer: MySQLContainer<*> =
            MySQLContainer(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("mss_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true)

        private val redisContainer: RedisContainer =
            RedisContainer(DockerImageName.parse("redis:7-alpine"))
                .withReuse(true)

        init {
            mysqlContainer.start()
            redisContainer.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mysqlContainer.jdbcUrl }
            registry.add("spring.datasource.username") { mysqlContainer.username }
            registry.add("spring.datasource.password") { mysqlContainer.password }
            registry.add("spring.datasource.driver-class-name") { "com.mysql.cj.jdbc.Driver" }
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.firstMappedPort }
        }
    }

    @Bean
    fun mysqlContainer(): MySQLContainer<*> = mysqlContainer

    @Bean
    fun redisContainer(): RedisContainer = redisContainer
}
