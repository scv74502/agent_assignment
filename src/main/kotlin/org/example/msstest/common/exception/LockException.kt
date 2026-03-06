package org.example.msstest.common.exception

sealed class LockException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class AcquisitionFailed(resourceId: String) :
        LockException(ErrorCode.LOCK_ACQUISITION_FAILED, "${ErrorCode.LOCK_ACQUISITION_FAILED.message}: $resourceId")
}
