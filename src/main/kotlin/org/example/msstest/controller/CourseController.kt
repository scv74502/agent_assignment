package org.example.msstest.controller

import org.example.msstest.controller.openapi.CourseApi
import org.example.msstest.domain.entity.CourseType
import org.example.msstest.dto.response.CourseResponse
import org.example.msstest.common.dto.CursorPageResponse
import org.example.msstest.service.CourseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseController(
    private val courseService: CourseService,
) : CourseApi {
    override fun getAllCourses(
        cursor: Long?,
        size: Int?,
        department: String?,
        courseType: CourseType?,
    ): ResponseEntity<CursorPageResponse<CourseResponse>> {
        val page = courseService.getAllCoursesPaged(cursor, size, department, courseType)
        return ResponseEntity.ok(page)
    }

    override fun getAvailableCourses(
        cursor: Long?,
        size: Int?,
        department: String?,
        courseType: CourseType?,
    ): ResponseEntity<CursorPageResponse<CourseResponse>> {
        val page = courseService.getAvailableCoursesPaged(cursor, size, department, courseType)
        return ResponseEntity.ok(page)
    }

    override fun getCourseById(courseId: Long): ResponseEntity<CourseResponse> {
        val course = courseService.getCourseById(courseId)
        return ResponseEntity.ok(course)
    }

    override fun getCoursesByProfessor(professorId: Long): ResponseEntity<List<CourseResponse>> {
        val courses = courseService.getCoursesByProfessor(professorId)
        return ResponseEntity.ok(courses)
    }
}
