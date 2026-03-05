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
    ) : EnrollmentException(ErrorCode.ALREADY_ENROLLED, "이미 수강신청한 강좌입니다: studentId=$studentId, courseId=$courseId")

    class NotFound(enrollmentId: Long) :
        EnrollmentException(ErrorCode.ENROLLMENT_NOT_FOUND, "수강신청 내역을 찾을 수 없습니다: $enrollmentId")

    class CreditLimitExceeded(
        currentCredits: Int,
        requestedCredits: Int,
        maxCredits: Int,
    ) : EnrollmentException(
        ErrorCode.CREDIT_LIMIT_EXCEEDED,
        "최대 학점을 초과했습니다: 현재=$currentCredits, 신청=$requestedCredits, 최대=$maxCredits",
    )

    class ScheduleConflict(
        courseId1: Long,
        courseId2: Long,
    ) : EnrollmentException(ErrorCode.SCHEDULE_CONFLICT, "시간표가 중복됩니다: courseId1=$courseId1, courseId2=$courseId2")

    class AlreadyCancelled(enrollmentId: Long) :
        EnrollmentException(ErrorCode.ALREADY_CANCELLED, "이미 취소된 수강신청입니다: $enrollmentId")
}
