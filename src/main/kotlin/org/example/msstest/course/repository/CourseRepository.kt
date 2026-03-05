package org.example.msstest.course.repository

import jakarta.persistence.LockModeType
import org.example.msstest.course.entity.Course
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CourseRepository : JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    fun findByCourseCode(courseCode: String): Optional<Course>

    fun existsByCourseCode(courseCode: String): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    fun findByIdWithLock(
        @Param("id") id: Long,
    ): Optional<Course>

    @Query("SELECT c FROM Course c JOIN FETCH c.professor WHERE c.currentEnrollment < c.capacity")
    fun findAvailableCourses(): List<Course>

    @Query("SELECT c FROM Course c JOIN FETCH c.professor JOIN FETCH c.schedules")
    fun findAllWithDetails(): List<Course>

    @Query("SELECT c FROM Course c JOIN FETCH c.professor WHERE c.professor.id = :professorId")
    fun findByProfessorId(
        @Param("professorId") professorId: Long,
    ): List<Course>
}
