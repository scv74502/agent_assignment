package org.example.msstest.exception

sealed class LockException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class AcquisitionFailed(resourceId: String) :
        LockException(ErrorCode.LOCK_ACQUISITION_FAILED, "락 획득에 실패했습니다: $resourceId")
}
