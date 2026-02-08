package org.example.msstest.exception

sealed class EnrollmentException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class StudentNotFound(studentId: Long) :
        EnrollmentException(ErrorCode.STUDENT_NOT_FOUND, "학생을 찾을 수 없습니다: $studentId")

    class StudentNotFoundByNo(studentNo: String) :
        EnrollmentException(ErrorCode.STUDENT_NOT_FOUND, "학생을 찾을 수 없습니다: $studentNo")

    class ProfessorNotFound(professorId: Long) :
        EnrollmentException(ErrorCode.PROFESSOR_NOT_FOUND, "교수를 찾을 수 없습니다: $professorId")

    class CourseNotFound(courseId: Long) :
        EnrollmentException(ErrorCode.COURSE_NOT_FOUND, "강좌를 찾을 수 없습니다: $courseId")

    class CourseFull(courseId: Long) :
        EnrollmentException(ErrorCode.COURSE_FULL, "강좌 정원이 초과되었습니다: $courseId")

    class AlreadyEnrolled(
        studentId: Long,
        courseId: Long,
    ) : EnrollmentException(ErrorCode.ALREADY_ENROLLED, "이미 수강신청한 강좌입니다: studentId=$studentId, courseId=$courseId")

    class EnrollmentNotFound(enrollmentId: Long) :
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

    class QueueFull(courseId: Long) :
        EnrollmentException(ErrorCode.QUEUE_FULL, "대기열이 가득 찼습니다: $courseId")

    class LockAcquisitionFailed(courseId: Long) :
        EnrollmentException(ErrorCode.LOCK_ACQUISITION_FAILED, "락 획득에 실패했습니다: $courseId")
}
