package org.example.msstest.enrollment.repository

import org.example.msstest.enrollment.entity.Enrollment
import org.example.msstest.enrollment.entity.EnrollmentStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface EnrollmentRepository : JpaRepository<Enrollment, Long> {
    fun findByStudentIdAndCourseIdAndStatus(
        studentId: Long,
        courseId: Long,
        status: EnrollmentStatus,
    ): Optional<Enrollment>

    fun existsByStudentIdAndCourseIdAndStatus(
        studentId: Long,
        courseId: Long,
        status: EnrollmentStatus,
    ): Boolean

    @Query(
        """
        SELECT e FROM Enrollment e
        JOIN FETCH e.course c
        JOIN FETCH c.professor
        WHERE e.student.id = :studentId AND e.status = :status
    """,
    )
    fun findByStudentIdAndStatus(
        @Param("studentId") studentId: Long,
        @Param("status") status: EnrollmentStatus,
    ): List<Enrollment>

    @Query(
        """
        SELECT COALESCE(SUM(c.credits), 0)
        FROM Enrollment e
        JOIN e.course c
        WHERE e.student.id = :studentId AND e.status = 'ENROLLED'
    """,
    )
    fun sumCreditsByStudentId(
        @Param("studentId") studentId: Long,
    ): Int

    fun countByCourseIdAndStatus(
        courseId: Long,
        status: EnrollmentStatus,
    ): Long
}
