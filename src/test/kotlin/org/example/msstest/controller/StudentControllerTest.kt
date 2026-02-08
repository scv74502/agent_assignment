package org.example.msstest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.msstest.dto.response.StudentResponse
import org.example.msstest.exception.GlobalExceptionHandler
import org.example.msstest.exception.StudentException
import org.example.msstest.service.StudentService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StudentController::class)
@Import(GlobalExceptionHandler::class)
@DisplayName("StudentController 테스트")
class StudentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var studentService: StudentService

    @Nested
    @DisplayName("GET /api/students")
    inner class GetAllStudents {
        @Test
        @WithMockUser
        @DisplayName("모든 학생 조회 성공")
        fun getAllStudents_success() {
            val students = listOf(
                StudentResponse(1L, "20240001", "홍길동", "컴퓨터공학과", 1),
                StudentResponse(2L, "20240002", "김철수", "전자공학과", 2),
            )
            whenever(studentService.getAllStudents()).thenReturn(students)

            mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].studentNo").value("20240001"))
                .andExpect(jsonPath("$[1].studentNo").value("20240002"))
        }
    }

    @Nested
    @DisplayName("GET /api/students/{studentId}")
    inner class GetStudentById {
        @Test
        @WithMockUser
        @DisplayName("학생 ID로 조회 성공")
        fun getStudentById_success() {
            val student = StudentResponse(1L, "20240001", "홍길동", "컴퓨터공학과", 1)
            whenever(studentService.getStudentById(1L)).thenReturn(student)

            mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentNo").value("20240001"))
                .andExpect(jsonPath("$.name").value("홍길동"))
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 학생 조회 시 404 반환")
        fun getStudentById_notFound() {
            whenever(studentService.getStudentById(999L)).thenThrow(StudentException.NotFound(999L))

            mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value("S001"))
        }
    }

    @Nested
    @DisplayName("GET /api/students/by-student-no/{studentNo}")
    inner class GetStudentByStudentNo {
        @Test
        @WithMockUser
        @DisplayName("학번으로 조회 성공")
        fun getStudentByStudentNo_success() {
            val student = StudentResponse(1L, "20240001", "홍길동", "컴퓨터공학과", 1)
            whenever(studentService.getStudentByStudentNo("20240001")).thenReturn(student)

            mockMvc.perform(get("/api/students/by-student-no/20240001"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.studentNo").value("20240001"))
        }
    }
}
