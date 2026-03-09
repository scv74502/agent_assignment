import com.redis.testcontainers.RedisContainer
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.testcontainers.utility.DockerImageName

abstract class TestContainerService :
    BuildService<BuildServiceParameters.None>,
    AutoCloseable {
    val redisContainer: RedisContainer =
        RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .apply { start() }

    override fun close() {
        redisContainer.stop()
    }
}
