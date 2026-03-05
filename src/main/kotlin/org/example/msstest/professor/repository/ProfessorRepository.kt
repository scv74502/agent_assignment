package org.example.msstest.professor.repository

import org.example.msstest.professor.entity.Professor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProfessorRepository : JpaRepository<Professor, Long> {
    fun findByProfessorNo(professorNo: String): Optional<Professor>

    fun existsByProfessorNo(professorNo: String): Boolean

    fun findByDepartment(department: String): List<Professor>
}
