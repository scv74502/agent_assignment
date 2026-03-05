package org.example.msstest.professor.service

import org.example.msstest.professor.dto.response.ProfessorResponse
import org.example.msstest.professor.exception.ProfessorException
import org.example.msstest.professor.repository.ProfessorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessorService(
    private val professorRepository: ProfessorRepository,
) {
    @Transactional(readOnly = true)
    fun getAllProfessors(): List<ProfessorResponse> = professorRepository.findAll().map { ProfessorResponse.from(it) }

    @Transactional(readOnly = true)
    fun getProfessorById(professorId: Long): ProfessorResponse {
        val professor =
            professorRepository.findById(professorId)
                .orElseThrow { ProfessorException.NotFound(professorId) }
        return ProfessorResponse.from(professor)
    }

    @Transactional(readOnly = true)
    fun getProfessorsByDepartment(department: String): List<ProfessorResponse> =
        professorRepository.findByDepartment(department)
            .map { ProfessorResponse.from(it) }
}
