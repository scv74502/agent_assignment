package org.example.msstest.course.entity

enum class CourseType(val displayName: String) {
    MAJOR_REQUIRED("전공필수"),
    MAJOR_ELECTIVE("전공선택"),
    GENERAL_REQUIRED("교양필수"),
    GENERAL_ELECTIVE("교양선택"),
}
