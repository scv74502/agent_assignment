package org.example.msstest.exception

sealed class ProfessorException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class NotFound(professorId: Long) :
        ProfessorException(ErrorCode.PROFESSOR_NOT_FOUND, "교수를 찾을 수 없습니다: $professorId")

    class Duplicate(professorNo: String) :
        ProfessorException(ErrorCode.DUPLICATE_PROFESSOR, "이미 존재하는 교수번호입니다: $professorNo")
}
