package org.example.msstest.repository

import org.example.msstest.domain.entity.CourseSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CourseScheduleRepository : JpaRepository<CourseSchedule, Long> {
    fun findByCourseId(courseId: Long): List<CourseSchedule>

    @Query(
        """
        SELECT cs FROM CourseSchedule cs
        JOIN FETCH cs.course c
        WHERE c.id IN (
            SELECT e.course.id FROM Enrollment e
            WHERE e.student.id = :studentId AND e.status = 'ENROLLED'
        )
    """,
    )
    fun findByStudentEnrollments(
        @Param("studentId") studentId: Long,
    ): List<CourseSchedule>
}
