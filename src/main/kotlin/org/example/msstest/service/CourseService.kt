package org.example.msstest.service

import org.example.msstest.dto.response.CourseResponse
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.repository.CourseRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepository: CourseRepository,
) {
    @Transactional(readOnly = true)
    fun getAllCourses(): List<CourseResponse> = courseRepository.findAllWithDetails().map { CourseResponse.from(it) }

    @Transactional(readOnly = true)
    fun getAvailableCourses(): List<CourseResponse> = courseRepository.findAvailableCourses().map { CourseResponse.from(it) }

    @Transactional(readOnly = true)
    fun getCourseById(courseId: Long): CourseResponse {
        val course =
            courseRepository.findById(courseId)
                .orElseThrow { EnrollmentException.CourseNotFound(courseId) }
        return CourseResponse.from(course)
    }

    @Transactional(readOnly = true)
    fun getCoursesByProfessor(professorId: Long): List<CourseResponse> =
        courseRepository.findByProfessorId(professorId)
            .map { CourseResponse.from(it) }
}
