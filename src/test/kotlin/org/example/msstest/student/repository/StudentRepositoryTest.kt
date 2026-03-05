package org.example.msstest.student.repository

import org.example.msstest.IntegrationTestBase
import org.example.msstest.student.entity.Student
import org.example.msstest.student.vo.StudentNo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@DisplayName("StudentRepository 테스트")
class StudentRepositoryTest : IntegrationTestBase() {
    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Nested
    @DisplayName("findByStudentNo")
    inner class FindByStudentNo {
        @Test
        @DisplayName("학번으로 학생 조회 성공")
        fun findByStudentNo_success() {
            val student = studentRepository.save(
                Student.create("20240001", "홍길동", "컴퓨터공학과", 1),
            )

            val result = studentRepository.findByStudentNo(StudentNo("20240001"))

            assertTrue(result.isPresent)
            assertEquals(student.id, result.get().id)
            assertEquals("홍길동", result.get().name)
        }

        @Test
        @DisplayName("존재하지 않는 학번 조회 시 빈 결과 반환")
        fun findByStudentNo_notFound() {
            val result = studentRepository.findByStudentNo(StudentNo("99999999"))

            assertFalse(result.isPresent)
        }
    }

    @Nested
    @DisplayName("existsByStudentNo")
    inner class ExistsByStudentNo {
        @Test
        @DisplayName("존재하는 학번 확인")
        fun existsByStudentNo_exists() {
            studentRepository.save(
                Student.create("20240001", "홍길동", "컴퓨터공학과", 1),
            )

            val result = studentRepository.existsByStudentNo(StudentNo("20240001"))

            assertTrue(result)
        }

        @Test
        @DisplayName("존재하지 않는 학번 확인")
        fun existsByStudentNo_notExists() {
            val result = studentRepository.existsByStudentNo(StudentNo("99999999"))

            assertFalse(result)
        }
    }
}
