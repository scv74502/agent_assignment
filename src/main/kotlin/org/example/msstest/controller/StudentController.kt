package org.example.msstest.controller

import org.example.msstest.controller.openapi.StudentApi
import org.example.msstest.dto.response.StudentResponse
import org.example.msstest.service.StudentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class StudentController(
    private val studentService: StudentService,
) : StudentApi {
    override fun getAllStudents(): ResponseEntity<List<StudentResponse>> {
        val students = studentService.getAllStudents()
        return ResponseEntity.ok(students)
    }

    override fun getStudentById(studentId: Long): ResponseEntity<StudentResponse> {
        val student = studentService.getStudentById(studentId)
        return ResponseEntity.ok(student)
    }

    override fun getStudentByStudentNo(studentNo: String): ResponseEntity<StudentResponse> {
        val student = studentService.getStudentByStudentNo(studentNo)
        return ResponseEntity.ok(student)
    }
}
