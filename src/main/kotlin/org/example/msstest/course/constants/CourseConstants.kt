package org.example.msstest.course.constants

object CourseConstants {
    const val BASE_PATH = "/api/v1/courses"
    const val PATH_AVAILABLE = "/available"
    const val PATH_COURSE_ID = "/{courseId}"

    const val TAG_NAME = "Course"
    const val TAG_DESCRIPTION = "강좌 API"

    const val ENROLLMENT_ZERO_MESSAGE = "수강 인원이 0명입니다"
    const val INVALID_CREDITS_MESSAGE = "학점은 1~6 사이여야 합니다"
    const val INVALID_PAGE_SIZE_MESSAGE = "페이지 크기는 1~%d 사이여야 합니다"
}
