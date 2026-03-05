package org.example.msstest.service

import org.example.msstest.domain.entity.CourseType
import org.example.msstest.dto.response.CourseResponse
import org.example.msstest.common.dto.CursorPageResponse
import org.example.msstest.exception.CourseException
import org.example.msstest.repository.CourseRepository
import org.example.msstest.repository.CourseSpecifications
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
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
                .orElseThrow { CourseException.NotFound(courseId) }
        return CourseResponse.from(course)
    }

    @Transactional(readOnly = true)
    fun getCoursesByProfessor(professorId: Long): List<CourseResponse> =
        courseRepository.findByProfessorId(professorId)
            .map { CourseResponse.from(it) }

    @Transactional(readOnly = true)
    fun getAllCoursesPaged(
        cursor: Long?,
        size: Int?,
        department: String?,
        courseType: CourseType?,
    ): CursorPageResponse<CourseResponse> {
        val pageSize = validatePageSize(size)
        val spec =
            Specification.where(CourseSpecifications.cursorAfter(cursor))
                .and(CourseSpecifications.departmentEquals(department))
                .and(CourseSpecifications.courseTypeEquals(courseType))
                .and(CourseSpecifications.fetchProfessor())

        val courses =
            courseRepository.findAll(
                spec,
                PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.ASC, "id")),
            ).content

        return CursorPageResponse.of(
            items = courses.map { CourseResponse.from(it) },
            pageSize = pageSize,
            cursorExtractor = { it.id },
        )
    }

    @Transactional(readOnly = true)
    fun getAvailableCoursesPaged(
        cursor: Long?,
        size: Int?,
        department: String?,
        courseType: CourseType?,
    ): CursorPageResponse<CourseResponse> {
        val pageSize = validatePageSize(size)
        val spec =
            Specification.where(CourseSpecifications.cursorAfter(cursor))
                .and(CourseSpecifications.isAvailable())
                .and(CourseSpecifications.departmentEquals(department))
                .and(CourseSpecifications.courseTypeEquals(courseType))
                .and(CourseSpecifications.fetchProfessor())

        val courses =
            courseRepository.findAll(
                spec,
                PageRequest.of(0, pageSize + 1, Sort.by(Sort.Direction.ASC, "id")),
            ).content

        return CursorPageResponse.of(
            items = courses.map { CourseResponse.from(it) },
            pageSize = pageSize,
            cursorExtractor = { it.id },
        )
    }

    private fun validatePageSize(size: Int?): Int {
        val pageSize = size ?: DEFAULT_PAGE_SIZE
        require(pageSize in 1..MAX_PAGE_SIZE) { "페이지 크기는 1~$MAX_PAGE_SIZE 사이여야 합니다" }
        return pageSize
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
    }
}
