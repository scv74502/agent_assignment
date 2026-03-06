package org.example.msstest.professor.exception

import org.example.msstest.common.exception.DomainBusinessException
import org.example.msstest.common.exception.ErrorCode

sealed class ProfessorException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class NotFound(professorId: Long) :
        ProfessorException(ErrorCode.PROFESSOR_NOT_FOUND, "${ErrorCode.PROFESSOR_NOT_FOUND.message}: $professorId")

    class Duplicate(professorNo: String) :
        ProfessorException(ErrorCode.DUPLICATE_PROFESSOR, "${ErrorCode.DUPLICATE_PROFESSOR.message}: $professorNo")
}
