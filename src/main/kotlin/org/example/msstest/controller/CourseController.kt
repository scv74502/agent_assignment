package org.example.msstest.controller

import org.example.msstest.controller.openapi.CourseApi
import org.example.msstest.dto.response.CourseResponse
import org.example.msstest.service.CourseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CourseController(
    private val courseService: CourseService,
) : CourseApi {
    override fun getAllCourses(): ResponseEntity<List<CourseResponse>> {
        val courses = courseService.getAllCourses()
        return ResponseEntity.ok(courses)
    }

    override fun getAvailableCourses(): ResponseEntity<List<CourseResponse>> {
        val courses = courseService.getAvailableCourses()
        return ResponseEntity.ok(courses)
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
