package org.example.msstest.common.lock

import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisLockService(
    private val redissonClient: RedissonClient,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun <T> executeWithLock(
        lockKey: String,
        waitTime: Long = 5L,
        leaseTime: Long = 10L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        action: () -> T,
    ): T? {
        val lock = redissonClient.getLock(lockKey)
        val acquired =
            try {
                lock.tryLock(waitTime, leaseTime, timeUnit)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn("Lock acquisition interrupted: $lockKey")
                return null
            }

        if (!acquired) {
            logger.warn("Failed to acquire lock: $lockKey")
            return null
        }

        return try {
            action()
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    fun tryLock(
        lockKey: String,
        waitTime: Long = 5L,
        leaseTime: Long = 10L,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
    ): Boolean {
        val lock = redissonClient.getLock(lockKey)
        return try {
            lock.tryLock(waitTime, leaseTime, timeUnit)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        }
    }

    fun unlock(lockKey: String) {
        val lock = redissonClient.getLock(lockKey)
        if (lock.isHeldByCurrentThread) {
            lock.unlock()
        }
    }

    companion object {
        fun enrollmentLockKey(studentId: Long): String = "enrollment:lock:student:$studentId"
    }
}
