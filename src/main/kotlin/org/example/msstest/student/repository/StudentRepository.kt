package org.example.msstest.student.repository

import org.example.msstest.student.entity.Student
import org.example.msstest.student.vo.StudentNo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByStudentNo(studentNo: StudentNo): Optional<Student>

    fun existsByStudentNo(studentNo: StudentNo): Boolean
}
