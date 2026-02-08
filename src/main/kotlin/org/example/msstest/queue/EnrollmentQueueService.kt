package org.example.msstest.queue

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class EnrollmentQueueService(
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${enrollment.queue-multiplier:100}") private val queueMultiplier: Int,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun enterQueue(
        courseId: Long,
        studentId: Long,
        remainingCapacity: Int,
    ): QueueEntry? {
        val maxQueueSize = remainingCapacity * queueMultiplier
        val queueKey = queueKey(courseId)
        val currentSize = redisTemplate.opsForZSet().size(queueKey) ?: 0

        if (currentSize >= maxQueueSize) {
            logger.info("Queue full for course $courseId: current=$currentSize, max=$maxQueueSize")
            return null
        }

        val token = UUID.randomUUID().toString()
        val score = System.currentTimeMillis().toDouble()
        val member = "$studentId:$token"

        redisTemplate.opsForZSet().add(queueKey, member, score)
        redisTemplate.expire(queueKey, Duration.ofHours(1))

        val position = redisTemplate.opsForZSet().rank(queueKey, member) ?: 0
        logger.info("Student $studentId entered queue for course $courseId at position $position")

        return QueueEntry(
            courseId = courseId,
            studentId = studentId,
            token = token,
            position = position.toInt(),
        )
    }

    fun getPosition(
        courseId: Long,
        studentId: Long,
        token: String,
    ): Int? {
        val queueKey = queueKey(courseId)
        val member = "$studentId:$token"
        return redisTemplate.opsForZSet().rank(queueKey, member)?.toInt()
    }

    fun leaveQueue(
        courseId: Long,
        studentId: Long,
        token: String,
    ) {
        val queueKey = queueKey(courseId)
        val member = "$studentId:$token"
        redisTemplate.opsForZSet().remove(queueKey, member)
        logger.info("Student $studentId left queue for course $courseId")
    }

    fun isFirstInQueue(
        courseId: Long,
        studentId: Long,
        token: String,
    ): Boolean {
        val position = getPosition(courseId, studentId, token)
        return position == 0
    }

    fun getQueueSize(courseId: Long): Long = redisTemplate.opsForZSet().size(queueKey(courseId)) ?: 0

    private fun queueKey(courseId: Long): String = "enrollment:queue:course:$courseId"
}

data class QueueEntry(
    val courseId: Long,
    val studentId: Long,
    val token: String,
    val position: Int,
)
