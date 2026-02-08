package org.example.msstest.repository

import org.example.msstest.IntegrationTestBase
import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.Enrollment
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.domain.entity.Professor
import org.example.msstest.domain.entity.Student
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("EnrollmentRepository 테스트")
class EnrollmentRepositoryTest : IntegrationTestBase() {
    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var professorRepository: ProfessorRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    private lateinit var student: Student
    private lateinit var professor: Professor
    private lateinit var course: Course

    @BeforeEach
    fun setup() {
        enrollmentRepository.deleteAll()
        courseRepository.deleteAll()
        studentRepository.deleteAll()
        professorRepository.deleteAll()

        professor = professorRepository.save(Professor.create("P001", "김교수", "컴퓨터공학과"))
        student = studentRepository.save(Student.create("20240001", "홍길동", "컴퓨터공학과", 1))
        course = courseRepository.save(Course.create("CS101", "자료구조", 3, 30, professor))
    }

    @Nested
    @DisplayName("existsByStudentIdAndCourseIdAndStatus")
    inner class ExistsByStudentIdAndCourseIdAndStatus {
        @Test
        @DisplayName("수강신청 존재 여부 확인 - 존재함")
        fun exists_true() {
            enrollmentRepository.save(Enrollment.create(student, course))

            val result = enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                student.id,
                course.id,
                EnrollmentStatus.ENROLLED,
            )

            assertTrue(result)
        }

        @Test
        @DisplayName("수강신청 존재 여부 확인 - 존재하지 않음")
        fun exists_false() {
            val result = enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                student.id,
                course.id,
                EnrollmentStatus.ENROLLED,
            )

            assertFalse(result)
        }
    }

    @Nested
    @DisplayName("sumCreditsByStudentId")
    inner class SumCreditsByStudentId {
        @Test
        @DisplayName("학생의 총 학점 계산")
        fun sumCredits() {
            val course2 = courseRepository.save(Course.create("CS102", "알고리즘", 3, 30, professor))

            enrollmentRepository.save(Enrollment.create(student, course))
            enrollmentRepository.save(Enrollment.create(student, course2))

            val result = enrollmentRepository.sumCreditsByStudentId(student.id)

            assertEquals(6, result)
        }

        @Test
        @DisplayName("수강신청이 없는 경우 0 반환")
        fun sumCredits_noEnrollment() {
            val result = enrollmentRepository.sumCreditsByStudentId(student.id)

            assertEquals(0, result)
        }
    }

    @Nested
    @DisplayName("findByStudentIdAndStatus")
    inner class FindByStudentIdAndStatus {
        @Test
        @DisplayName("학생의 수강신청 목록 조회")
        fun findByStudentIdAndStatus() {
            enrollmentRepository.save(Enrollment.create(student, course))

            val result = enrollmentRepository.findByStudentIdAndStatus(student.id, EnrollmentStatus.ENROLLED)

            assertEquals(1, result.size)
            assertEquals(course.id, result[0].course.id)
        }
    }
}
