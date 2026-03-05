package org.example.msstest.course.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Credits Value Object 테스트")
class CreditsTest {
    @Test
    @DisplayName("유효한 학점 (1~6) 생성 성공")
    fun create_validCredits() {
        for (value in 1..6) {
            val credits = Credits(value)
            assertEquals(value, credits.value)
        }
    }

    @Test
    @DisplayName("0 학점은 생성 실패")
    fun create_zeroCredits_fails() {
        assertThrows<IllegalArgumentException> {
            Credits(0)
        }
    }

    @Test
    @DisplayName("음수 학점은 생성 실패")
    fun create_negativeCredits_fails() {
        assertThrows<IllegalArgumentException> {
            Credits(-1)
        }
    }

    @Test
    @DisplayName("7 이상 학점은 생성 실패")
    fun create_tooManyCredits_fails() {
        assertThrows<IllegalArgumentException> {
            Credits(7)
        }
    }

    @Test
    @DisplayName("학점 더하기 연산")
    fun plus_credits() {
        val credits1 = Credits(3)
        val credits2 = Credits(2)
        val sum = credits1 + credits2

        assertEquals(5, sum.value)
    }

    @Test
    @DisplayName("학점과 정수 비교")
    fun compareTo_int() {
        val credits = Credits(3)

        assertEquals(0, credits.compareTo(3))
        assertEquals(1, credits.compareTo(2))
        assertEquals(-1, credits.compareTo(4))
    }
}
