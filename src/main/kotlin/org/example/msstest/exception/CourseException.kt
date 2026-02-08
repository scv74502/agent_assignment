package org.example.msstest.exception

sealed class CourseException(
    val errorCode: ErrorCode,
    override val message: String = errorCode.message,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class NotFound(courseId: Long) :
        CourseException(ErrorCode.COURSE_NOT_FOUND, "강좌를 찾을 수 없습니다: $courseId")

    class Full(courseId: Long) :
        CourseException(ErrorCode.COURSE_FULL, "강좌 정원이 초과되었습니다: $courseId")

    class Duplicate(courseCode: String) :
        CourseException(ErrorCode.DUPLICATE_COURSE, "이미 존재하는 과목코드입니다: $courseCode")
}
