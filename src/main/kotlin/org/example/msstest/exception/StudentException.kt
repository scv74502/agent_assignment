package org.example.msstest.exception

sealed class StudentException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class NotFound(studentId: Long) :
        StudentException(ErrorCode.STUDENT_NOT_FOUND, "학생을 찾을 수 없습니다: $studentId")

    class NotFoundByNo(studentNo: String) :
        StudentException(ErrorCode.STUDENT_NOT_FOUND, "학생을 찾을 수 없습니다: $studentNo")

    class Duplicate(studentNo: String) :
        StudentException(ErrorCode.DUPLICATE_STUDENT, "이미 존재하는 학번입니다: $studentNo")
}
