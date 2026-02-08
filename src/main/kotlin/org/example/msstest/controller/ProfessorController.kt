package org.example.msstest.controller

import org.example.msstest.controller.openapi.ProfessorApi
import org.example.msstest.dto.response.ProfessorResponse
import org.example.msstest.service.ProfessorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfessorController(
    private val professorService: ProfessorService,
) : ProfessorApi {
    override fun getAllProfessors(): ResponseEntity<List<ProfessorResponse>> {
        val professors = professorService.getAllProfessors()
        return ResponseEntity.ok(professors)
    }

    override fun getProfessorById(professorId: Long): ResponseEntity<ProfessorResponse> {
        val professor = professorService.getProfessorById(professorId)
        return ResponseEntity.ok(professor)
    }

    override fun getProfessorsByDepartment(department: String): ResponseEntity<List<ProfessorResponse>> {
        val professors = professorService.getProfessorsByDepartment(department)
        return ResponseEntity.ok(professors)
    }
}
