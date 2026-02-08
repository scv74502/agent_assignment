package org.example.msstest.dto.response

import org.example.msstest.domain.entity.Professor

data class ProfessorResponse(
    val id: Long,
    val professorNo: String,
    val name: String,
    val department: String,
) {
    companion object {
        fun from(professor: Professor): ProfessorResponse =
            ProfessorResponse(
                id = professor.id,
                professorNo = professor.professorNo.value,
                name = professor.name,
                department = professor.department,
            )
    }
}
