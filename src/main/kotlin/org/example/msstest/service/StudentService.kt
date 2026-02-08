package org.example.msstest.service

import org.example.msstest.dto.response.StudentResponse
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService(
    private val studentRepository: StudentRepository,
) {
    @Transactional(readOnly = true)
    fun getAllStudents(): List<StudentResponse> = studentRepository.findAll().map { StudentResponse.from(it) }

    @Transactional(readOnly = true)
    fun getStudentById(studentId: Long): StudentResponse {
        val student =
            studentRepository.findById(studentId)
                .orElseThrow { EnrollmentException.StudentNotFound(studentId) }
        return StudentResponse.from(student)
    }

    @Transactional(readOnly = true)
    fun getStudentByStudentNo(studentNo: String): StudentResponse {
        val student =
            studentRepository.findByStudentNo(studentNo)
                .orElseThrow { EnrollmentException.StudentNotFoundByNo(studentNo) }
        return StudentResponse.from(student)
    }
}
