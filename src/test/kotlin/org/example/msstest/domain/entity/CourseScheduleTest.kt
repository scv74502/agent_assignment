package org.example.msstest.domain.entity

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.DayOfWeek
import java.time.LocalTime

@DisplayName("CourseSchedule 시간 중복 테스트")
class CourseScheduleTest {
    private fun createSchedule(
        dayOfWeek: DayOfWeek,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int,
    ): CourseSchedule {
        val professor = Professor.create("P001", "교수", "컴퓨터공학과")
        val course = Course.create("CS101", "자료구조", 3, 30, professor)
        return CourseSchedule.create(
            course = course,
            dayOfWeek = dayOfWeek,
            startTime = LocalTime.of(startHour, startMinute),
            endTime = LocalTime.of(endHour, endMinute),
            location = "공학관 101",
        )
    }

    @Nested
    @DisplayName("시간 중복 검사")
    inner class TimeOverlapTests {
        @Test
        @DisplayName("같은 요일에 시간이 완전히 겹치면 중복")
        fun overlaps_whenSameDayAndTimeOverlaps() {
            val schedule1 = createSchedule(DayOfWeek.MONDAY, 10, 0, 12, 0)
            val schedule2 = createSchedule(DayOfWeek.MONDAY, 11, 0, 13, 0)

            assertTrue(schedule1.overlaps(schedule2))
            assertTrue(schedule2.overlaps(schedule1))
        }

        @Test
        @DisplayName("같은 요일에 한 시간이 다른 시간에 포함되면 중복")
        fun overlaps_whenOneContainsAnother() {
            val schedule1 = createSchedule(DayOfWeek.MONDAY, 9, 0, 14, 0)
            val schedule2 = createSchedule(DayOfWeek.MONDAY, 10, 0, 12, 0)

            assertTrue(schedule1.overlaps(schedule2))
            assertTrue(schedule2.overlaps(schedule1))
        }

        @Test
        @DisplayName("같은 요일에 종료-시작 시간이 같으면 중복 아님 (경계 허용)")
        fun noOverlap_whenEndEqualsStart() {
            val schedule1 = createSchedule(DayOfWeek.MONDAY, 9, 0, 11, 0)
            val schedule2 = createSchedule(DayOfWeek.MONDAY, 11, 0, 13, 0)

            assertFalse(schedule1.overlaps(schedule2))
            assertFalse(schedule2.overlaps(schedule1))
        }

        @Test
        @DisplayName("같은 요일에 시간이 완전히 분리되면 중복 아님")
        fun noOverlap_whenCompletelySepatated() {
            val schedule1 = createSchedule(DayOfWeek.MONDAY, 9, 0, 10, 0)
            val schedule2 = createSchedule(DayOfWeek.MONDAY, 14, 0, 16, 0)

            assertFalse(schedule1.overlaps(schedule2))
            assertFalse(schedule2.overlaps(schedule1))
        }

        @Test
        @DisplayName("다른 요일이면 시간이 같아도 중복 아님")
        fun noOverlap_whenDifferentDays() {
            val schedule1 = createSchedule(DayOfWeek.MONDAY, 10, 0, 12, 0)
            val schedule2 = createSchedule(DayOfWeek.TUESDAY, 10, 0, 12, 0)

            assertFalse(schedule1.overlaps(schedule2))
            assertFalse(schedule2.overlaps(schedule1))
        }

        @Test
        @DisplayName("분 단위 경계 테스트 - 30분 차이로 겹침")
        fun overlaps_whenMinutesOverlap() {
            val schedule1 = createSchedule(DayOfWeek.WEDNESDAY, 10, 0, 11, 30)
            val schedule2 = createSchedule(DayOfWeek.WEDNESDAY, 11, 0, 12, 30)

            assertTrue(schedule1.overlaps(schedule2))
        }

        @Test
        @DisplayName("분 단위 경계 테스트 - 정확히 연속하면 중복 아님")
        fun noOverlap_whenExactlyConsecutiveMinutes() {
            val schedule1 = createSchedule(DayOfWeek.WEDNESDAY, 10, 0, 11, 30)
            val schedule2 = createSchedule(DayOfWeek.WEDNESDAY, 11, 30, 13, 0)

            assertFalse(schedule1.overlaps(schedule2))
        }
    }
}
