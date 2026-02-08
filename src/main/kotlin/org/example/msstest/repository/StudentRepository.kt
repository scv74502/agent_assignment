package org.example.msstest.repository

import org.example.msstest.domain.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByStudentNo(studentNo: String): Optional<Student>

    fun existsByStudentNo(studentNo: String): Boolean
}
