package org.example.msstest

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
abstract class IntegrationTestBase(
    private val containerConfig: ContainerConfig = resolveContainerConfig(),
) {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    companion object {
        private fun resolveContainerConfig(): ContainerConfig {
            val redisHost = System.getProperty("tc.redis.host")
            if (redisHost != null) {
                return ContainerConfig(
                    redisHost = redisHost,
                    redisPort = System.getProperty("tc.redis.port").toInt(),
                )
            }
            return ContainerConfig(
                redisHost = TestContainerSingletons.redisContainer.host,
                redisPort = TestContainerSingletons.redisContainer.firstMappedPort,
            )
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            val config = resolveContainerConfig()
            registry.add("spring.data.redis.host") { config.redisHost }
            registry.add("spring.data.redis.port") { config.redisPort }
        }
    }

    @BeforeEach
    fun clearRedis() {
        redisTemplate.connectionFactory?.connection?.serverCommands()?.flushAll()
    }
}
