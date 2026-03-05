package org.example.msstest.course.service

import org.example.msstest.IntegrationTestBase
import org.example.msstest.course.entity.Course
import org.example.msstest.course.entity.CourseType
import org.example.msstest.course.repository.CourseRepository
import org.example.msstest.course.repository.CourseScheduleRepository
import org.example.msstest.enrollment.repository.EnrollmentRepository
import org.example.msstest.professor.entity.Professor
import org.example.msstest.professor.repository.ProfessorRepository
import org.example.msstest.student.repository.StudentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("CourseService 페이지네이션 테스트")
class CourseServiceTest : IntegrationTestBase() {
    @Autowired
    private lateinit var courseService: CourseService

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var professorRepository: ProfessorRepository

    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Autowired
    private lateinit var courseScheduleRepository: CourseScheduleRepository

    @Autowired
    private lateinit var studentRepository: StudentRepository

    private lateinit var csProfessor: Professor
    private lateinit var mathProfessor: Professor

    @BeforeEach
    fun setup() {
        csProfessor = professorRepository.save(Professor.create("P001", "김교수", "컴퓨터공학과"))
        mathProfessor = professorRepository.save(Professor.create("P002", "이교수", "수학과"))

        // 컴퓨터공학과 전공필수 5개
        (1..5).forEach { i ->
            courseRepository.save(
                Course.create("CS${100 + i}", "CS과목$i", 3, 30, csProfessor, CourseType.MAJOR_REQUIRED, "컴퓨터공학과"),
            )
        }
        // 컴퓨터공학과 전공선택 3개
        (1..3).forEach { i ->
            courseRepository.save(
                Course.create("CS${200 + i}", "CS선택$i", 3, 30, csProfessor, CourseType.MAJOR_ELECTIVE, "컴퓨터공학과"),
            )
        }
        // 수학과 교양필수 2개
        (1..2).forEach { i ->
            courseRepository.save(
                Course.create("MA${100 + i}", "수학과목$i", 3, 50, mathProfessor, CourseType.GENERAL_REQUIRED, "수학과"),
            )
        }
    }

    @Nested
    @DisplayName("전체 강좌 페이지네이션")
    inner class GetAllCoursesPaged {
        @Test
        @DisplayName("기본 페이지 크기로 조회")
        fun defaultPaging() {
            val result = courseService.getAllCoursesPaged(null, null, null, null)

            assertEquals(10, result.size)
            assertFalse(result.hasNext)
            assertNull(result.nextCursor)
        }

        @Test
        @DisplayName("페이지 크기 지정하여 순회")
        fun pagingWithSize() {
            val page1 = courseService.getAllCoursesPaged(null, 3, null, null)
            assertEquals(3, page1.size)
            assertTrue(page1.hasNext)

            val page2 = courseService.getAllCoursesPaged(page1.nextCursor, 3, null, null)
            assertEquals(3, page2.size)
            assertTrue(page2.hasNext)

            val page3 = courseService.getAllCoursesPaged(page2.nextCursor, 3, null, null)
            assertEquals(3, page3.size)
            assertTrue(page3.hasNext)

            val page4 = courseService.getAllCoursesPaged(page3.nextCursor, 3, null, null)
            assertEquals(1, page4.size)
            assertFalse(page4.hasNext)
        }

        @Test
        @DisplayName("페이지 간 중복 없음")
        fun noDuplicateAcrossPages() {
            val allIds = mutableSetOf<Long>()
            var cursor: Long? = null
            do {
                val page = courseService.getAllCoursesPaged(cursor, 4, null, null)
                page.items.forEach { item ->
                    assertTrue(allIds.add(item.id), "중복 ID 발견: ${item.id}")
                }
                cursor = page.nextCursor
            } while (page.hasNext)

            assertEquals(10, allIds.size)
        }

        @Test
        @DisplayName("학과 필터링")
        fun filterByDepartment() {
            val result = courseService.getAllCoursesPaged(null, null, "컴퓨터공학과", null)

            assertEquals(8, result.size)
            assertTrue(result.items.all { it.department == "컴퓨터공학과" })
        }

        @Test
        @DisplayName("이수구분 필터링")
        fun filterByCourseType() {
            val result = courseService.getAllCoursesPaged(null, null, null, CourseType.MAJOR_REQUIRED)

            assertEquals(5, result.size)
            assertTrue(result.items.all { it.courseType == "MAJOR_REQUIRED" })
        }

        @Test
        @DisplayName("학과 + 이수구분 복합 필터")
        fun filterByCombined() {
            val result = courseService.getAllCoursesPaged(null, null, "컴퓨터공학과", CourseType.MAJOR_ELECTIVE)

            assertEquals(3, result.size)
            assertTrue(result.items.all { it.department == "컴퓨터공학과" && it.courseType == "MAJOR_ELECTIVE" })
        }

        @Test
        @DisplayName("필터 + 페이지네이션 조합")
        fun filterWithPaging() {
            val page1 = courseService.getAllCoursesPaged(null, 3, "컴퓨터공학과", CourseType.MAJOR_REQUIRED)
            assertEquals(3, page1.size)
            assertTrue(page1.hasNext)

            val page2 = courseService.getAllCoursesPaged(page1.nextCursor, 3, "컴퓨터공학과", CourseType.MAJOR_REQUIRED)
            assertEquals(2, page2.size)
            assertFalse(page2.hasNext)
        }

        @Test
        @DisplayName("결과 없는 필터")
        fun emptyResult() {
            val result = courseService.getAllCoursesPaged(null, null, "영어영문학과", null)

            assertEquals(0, result.size)
            assertFalse(result.hasNext)
            assertNull(result.nextCursor)
        }
    }

    @Nested
    @DisplayName("수강 가능 강좌 페이지네이션")
    inner class GetAvailableCoursesPaged {
        @Test
        @DisplayName("정원이 꽉 찬 강좌 제외")
        fun excludeFullCourses() {
            // 첫 번째 CS 과목의 정원을 꽉 채움
            val firstCourse = courseRepository.findAll().first { it.courseCode.value.startsWith("CS1") }
            repeat(firstCourse.capacity) { firstCourse.incrementEnrollment() }
            courseRepository.save(firstCourse)

            val result = courseService.getAvailableCoursesPaged(null, null, null, null)

            assertEquals(9, result.size)
            assertTrue(result.items.none { it.id == firstCourse.id })
        }

        @Test
        @DisplayName("수강 가능 + 필터 조합")
        fun availableWithFilter() {
            val result = courseService.getAvailableCoursesPaged(null, null, "수학과", CourseType.GENERAL_REQUIRED)

            assertEquals(2, result.size)
            assertTrue(result.items.all { it.department == "수학과" })
        }
    }

    @Nested
    @DisplayName("페이지 크기 검증")
    inner class PageSizeValidation {
        @Test
        @DisplayName("size=0 이면 에러")
        fun sizeZero() {
            assertThrows<IllegalArgumentException> {
                courseService.getAllCoursesPaged(null, 0, null, null)
            }
        }

        @Test
        @DisplayName("size=101 이면 에러")
        fun sizeTooLarge() {
            assertThrows<IllegalArgumentException> {
                courseService.getAllCoursesPaged(null, 101, null, null)
            }
        }

        @Test
        @DisplayName("size=100 은 허용")
        fun sizeMax() {
            val result = courseService.getAllCoursesPaged(null, 100, null, null)
            assertEquals(10, result.size)
        }
    }
}
