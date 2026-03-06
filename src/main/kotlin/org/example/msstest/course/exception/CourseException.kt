package org.example.msstest.course.exception

import org.example.msstest.common.exception.DomainBusinessException
import org.example.msstest.common.exception.ErrorCode

sealed class CourseException(
    override val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause), DomainBusinessException {
    class NotFound(courseId: Long) :
        CourseException(ErrorCode.COURSE_NOT_FOUND, "${ErrorCode.COURSE_NOT_FOUND.message}: $courseId")

    class Full(courseId: Long) :
        CourseException(ErrorCode.COURSE_FULL, "${ErrorCode.COURSE_FULL.message}: $courseId")

    class Duplicate(courseCode: String) :
        CourseException(ErrorCode.DUPLICATE_COURSE, "${ErrorCode.DUPLICATE_COURSE.message}: $courseCode")
}
