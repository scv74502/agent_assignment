package org.example.msstest.enrollment.exception

import org.example.msstest.common.exception.DomainBusinessException
import org.example.msstest.common.exception.ErrorCode

sealed class EnrollmentException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class AlreadyEnrolled(
        studentId: Long,
        courseId: Long,
    ) : EnrollmentException(
        ErrorCode.ALREADY_ENROLLED,
        "${ErrorCode.ALREADY_ENROLLED.message}: studentId=$studentId, courseId=$courseId",
    )

    class NotFound(enrollmentId: Long) :
        EnrollmentException(ErrorCode.ENROLLMENT_NOT_FOUND, "${ErrorCode.ENROLLMENT_NOT_FOUND.message}: $enrollmentId")

    class CreditLimitExceeded(
        currentCredits: Int,
        requestedCredits: Int,
        maxCredits: Int,
    ) : EnrollmentException(
        ErrorCode.CREDIT_LIMIT_EXCEEDED,
        "${ErrorCode.CREDIT_LIMIT_EXCEEDED.message}: 현재=$currentCredits, 신청=$requestedCredits, 최대=$maxCredits",
    )

    class ScheduleConflict(
        courseId1: Long,
        courseId2: Long,
    ) : EnrollmentException(
        ErrorCode.SCHEDULE_CONFLICT,
        "${ErrorCode.SCHEDULE_CONFLICT.message}: courseId1=$courseId1, courseId2=$courseId2",
    )

    class AlreadyCancelled(enrollmentId: Long) :
        EnrollmentException(ErrorCode.ALREADY_CANCELLED, "${ErrorCode.ALREADY_CANCELLED.message}: $enrollmentId")
}
