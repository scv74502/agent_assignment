package org.example.msstest.common.exception

sealed class QueueException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class Full(resourceId: String) :
        QueueException(ErrorCode.QUEUE_FULL, "대기열이 가득 찼습니다: $resourceId")

    class Timeout(resourceId: String) :
        QueueException(ErrorCode.QUEUE_TIMEOUT, "대기 시간이 초과되었습니다: $resourceId")
}
