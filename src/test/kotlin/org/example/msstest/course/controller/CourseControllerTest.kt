package org.example.msstest.course.controller

import org.example.msstest.common.dto.CursorPageResponse
import org.example.msstest.common.exception.GlobalExceptionHandler
import org.example.msstest.course.dto.response.CourseResponse
import org.example.msstest.course.entity.CourseType
import org.example.msstest.course.service.CourseService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CourseController::class)
@Import(GlobalExceptionHandler::class)
@DisplayName("CourseController 테스트")
class CourseControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var courseService: CourseService

    private fun sampleCourseResponse(id: Long) =
        CourseResponse(
            id = id,
            courseCode = "CS${100 + id}",
            courseName = "테스트과목$id",
            credits = 3,
            capacity = 30,
            currentEnrollment = 10,
            remainingCapacity = 20,
            courseType = "MAJOR_REQUIRED",
            courseTypeDisplay = "전공필수",
            department = "컴퓨터공학과",
            professorId = 1L,
            professorName = "김교수",
            schedule = "월 10:00~11:50",
            schedules = emptyList(),
        )

    @Nested
    @DisplayName("GET /api/v1/courses")
    inner class GetAllCourses {
        @Test
        @WithMockUser
        @DisplayName("기본 조회 성공")
        fun getAllCourses_default() {
            val page =
                CursorPageResponse(
                    items = listOf(sampleCourseResponse(1)),
                    nextCursor = null,
                    hasNext = false,
                    size = 1,
                )
            `when`(courseService.getAllCoursesPaged(null, null, null, null)).thenReturn(page)

            mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items").isArray)
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].courseType").value("MAJOR_REQUIRED"))
                .andExpect(jsonPath("$.items[0].department").value("컴퓨터공학과"))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.nextCursor").isEmpty)
                .andExpect(jsonPath("$.size").value(1))
        }

        @Test
        @WithMockUser
        @DisplayName("커서와 필터 파라미터 전달")
        fun getAllCourses_withParams() {
            val page =
                CursorPageResponse(
                    items = listOf(sampleCourseResponse(5)),
                    nextCursor = 5L,
                    hasNext = true,
                    size = 1,
                )
            `when`(
                courseService.getAllCoursesPaged(3L, 1, "컴퓨터공학과", CourseType.MAJOR_REQUIRED),
            ).thenReturn(page)

            mockMvc.perform(
                get("/api/v1/courses")
                    .param("cursor", "3")
                    .param("size", "1")
                    .param("department", "컴퓨터공학과")
                    .param("courseType", "MAJOR_REQUIRED"),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items[0].id").value(5))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.nextCursor").value(5))
        }

        @Test
        @WithMockUser
        @DisplayName("잘못된 courseType 입력 시 400")
        fun getAllCourses_invalidCourseType() {
            mockMvc.perform(
                get("/api/v1/courses")
                    .param("courseType", "INVALID_TYPE"),
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("GET /api/v1/courses/available")
    inner class GetAvailableCourses {
        @Test
        @WithMockUser
        @DisplayName("수강 가능 강좌 기본 조회")
        fun getAvailableCourses_default() {
            val page =
                CursorPageResponse(
                    items = listOf(sampleCourseResponse(2)),
                    nextCursor = null,
                    hasNext = false,
                    size = 1,
                )
            `when`(courseService.getAvailableCoursesPaged(null, null, null, null)).thenReturn(page)

            mockMvc.perform(get("/api/v1/courses/available"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items[0].id").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
        }

        @Test
        @WithMockUser
        @DisplayName("수강 가능 강좌 필터 조회")
        fun getAvailableCourses_withFilters() {
            val page =
                CursorPageResponse<CourseResponse>(
                    items = emptyList(),
                    nextCursor = null,
                    hasNext = false,
                    size = 0,
                )
            `when`(
                courseService.getAvailableCoursesPaged(null, null, "수학과", CourseType.GENERAL_ELECTIVE),
            ).thenReturn(page)

            mockMvc.perform(
                get("/api/v1/courses/available")
                    .param("department", "수학과")
                    .param("courseType", "GENERAL_ELECTIVE"),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items").isEmpty)
                .andExpect(jsonPath("$.size").value(0))
        }
    }
}
