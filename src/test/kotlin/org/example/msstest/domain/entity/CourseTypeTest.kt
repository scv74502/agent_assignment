package org.example.msstest.domain.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("CourseType 테스트")
class CourseTypeTest {
    @Test
    @DisplayName("4가지 이수구분 존재")
    fun hasAllValues() {
        assertEquals(4, CourseType.entries.size)
    }

    @Test
    @DisplayName("한글 표시명 매핑")
    fun displayNames() {
        assertEquals("전공필수", CourseType.MAJOR_REQUIRED.displayName)
        assertEquals("전공선택", CourseType.MAJOR_ELECTIVE.displayName)
        assertEquals("교양필수", CourseType.GENERAL_REQUIRED.displayName)
        assertEquals("교양선택", CourseType.GENERAL_ELECTIVE.displayName)
    }

    @Test
    @DisplayName("valueOf 변환")
    fun valueOfWorks() {
        assertEquals(CourseType.MAJOR_REQUIRED, CourseType.valueOf("MAJOR_REQUIRED"))
        assertEquals(CourseType.GENERAL_ELECTIVE, CourseType.valueOf("GENERAL_ELECTIVE"))
    }
}
