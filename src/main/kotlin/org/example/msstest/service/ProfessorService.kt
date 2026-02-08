package org.example.msstest.service

import org.example.msstest.dto.response.ProfessorResponse
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.repository.ProfessorRepository
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
                .orElseThrow { EnrollmentException.ProfessorNotFound(professorId) }
        return ProfessorResponse.from(professor)
    }

    @Transactional(readOnly = true)
    fun getProfessorsByDepartment(department: String): List<ProfessorResponse> =
        professorRepository.findByDepartment(department)
            .map { ProfessorResponse.from(it) }
}
