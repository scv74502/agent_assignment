package org.example.msstest.student.exception

import org.example.msstest.common.exception.DomainBusinessException
import org.example.msstest.common.exception.ErrorCode

sealed class StudentException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class NotFound(studentId: Long) :
        StudentException(ErrorCode.STUDENT_NOT_FOUND, "${ErrorCode.STUDENT_NOT_FOUND.message}: $studentId")

    class NotFoundByNo(studentNo: String) :
        StudentException(ErrorCode.STUDENT_NOT_FOUND, "${ErrorCode.STUDENT_NOT_FOUND.message}: $studentNo")

    class Duplicate(studentNo: String) :
        StudentException(ErrorCode.DUPLICATE_STUDENT, "${ErrorCode.DUPLICATE_STUDENT.message}: $studentNo")
}
