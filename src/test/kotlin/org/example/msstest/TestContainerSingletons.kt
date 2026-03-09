package org.example.msstest

import com.redis.testcontainers.RedisContainer
import org.testcontainers.utility.DockerImageName

object TestContainerSingletons {
    val redisContainer: RedisContainer by lazy {
        RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)
            .apply { start() }
    }
}
