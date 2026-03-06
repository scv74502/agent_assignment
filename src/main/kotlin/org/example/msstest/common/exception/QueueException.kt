package org.example.msstest.common.exception

sealed class QueueException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class Full(resourceId: String) :
        QueueException(ErrorCode.QUEUE_FULL, "${ErrorCode.QUEUE_FULL.message}: $resourceId")

    class Timeout(resourceId: String) :
        QueueException(ErrorCode.QUEUE_TIMEOUT, "${ErrorCode.QUEUE_TIMEOUT.message}: $resourceId")
}
