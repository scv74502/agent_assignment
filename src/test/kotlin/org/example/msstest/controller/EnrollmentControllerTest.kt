package org.example.msstest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.dto.request.CancelEnrollmentRequest
import org.example.msstest.dto.request.EnrollmentRequest
import org.example.msstest.dto.response.EnrollmentResponse
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.exception.GlobalExceptionHandler
import org.example.msstest.exception.StudentException
import org.example.msstest.service.EnrollmentService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(EnrollmentController::class)
@Import(GlobalExceptionHandler::class)
@DisplayName("EnrollmentController 테스트")
class EnrollmentControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var enrollmentService: EnrollmentService

    @Nested
    @DisplayName("POST /api/v1/enrollments")
    inner class Enroll {
        @Test
        @WithMockUser
        @DisplayName("수강신청 성공")
        fun enroll_success() {
            val request = EnrollmentRequest(studentId = 1L, courseId = 1L)
            val response = EnrollmentResponse(
                id = 1L,
                studentId = 1L,
                studentName = "홍길동",
                courseId = 1L,
                courseName = "자료구조",
                courseCode = "CS101",
                credits = 3,
                professorName = "김교수",
                status = EnrollmentStatus.ENROLLED,
                enrolledAt = LocalDateTime.now(),
            )
            `when`(enrollmentService.enroll(1L, 1L)).thenReturn(response)

            mockMvc.perform(
                post("/api/v1/enrollments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.status").value("ENROLLED"))
        }

        @Test
        @WithMockUser
        @DisplayName("중복 수강신청 시 409 반환")
        fun enroll_alreadyEnrolled() {
            val request = EnrollmentRequest(studentId = 1L, courseId = 1L)
            `when`(enrollmentService.enroll(1L, 1L))
                .thenThrow(EnrollmentException.AlreadyEnrolled(1L, 1L))

            mockMvc.perform(
                post("/api/v1/enrollments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andExpect(status().isConflict)
                .andExpect(jsonPath("$.code").value("E001"))
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/enrollments")
    inner class Cancel {
        @Test
        @WithMockUser
        @DisplayName("수강취소 성공")
        fun cancel_success() {
            val request = CancelEnrollmentRequest(studentId = 1L, courseId = 1L)
            val response = EnrollmentResponse(
                id = 1L,
                studentId = 1L,
                studentName = "홍길동",
                courseId = 1L,
                courseName = "자료구조",
                courseCode = "CS101",
                credits = 3,
                professorName = "김교수",
                status = EnrollmentStatus.CANCELLED,
                enrolledAt = LocalDateTime.now(),
            )
            `when`(enrollmentService.cancel(1L, 1L)).thenReturn(response)

            mockMvc.perform(
                delete("/api/v1/enrollments")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("CANCELLED"))
        }
    }

    @Nested
    @DisplayName("GET /api/v1/enrollments/students/{studentId}")
    inner class GetEnrollmentsByStudent {
        @Test
        @WithMockUser
        @DisplayName("학생별 수강신청 목록 조회 성공")
        fun getEnrollmentsByStudent_success() {
            val responses = listOf(
                EnrollmentResponse(
                    id = 1L,
                    studentId = 1L,
                    studentName = "홍길동",
                    courseId = 1L,
                    courseName = "자료구조",
                    courseCode = "CS101",
                    credits = 3,
                    professorName = "김교수",
                    status = EnrollmentStatus.ENROLLED,
                    enrolledAt = LocalDateTime.now(),
                ),
            )
            `when`(enrollmentService.getEnrollmentsByStudent(1L)).thenReturn(responses)

            mockMvc.perform(get("/api/v1/enrollments/students/1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value(1))
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 학생 조회 시 404 반환")
        fun getEnrollmentsByStudent_studentNotFound() {
            `when`(enrollmentService.getEnrollmentsByStudent(999L))
                .thenThrow(StudentException.NotFound(999L))

            mockMvc.perform(get("/api/v1/enrollments/students/999"))
                .andExpect(status().isNotFound)
                .andExpect(jsonPath("$.code").value("S001"))
        }
    }
}
