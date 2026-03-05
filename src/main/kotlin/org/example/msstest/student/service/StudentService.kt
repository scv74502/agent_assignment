package org.example.msstest.student.service

import org.example.msstest.student.dto.response.StudentResponse
import org.example.msstest.student.entity.Student
import org.example.msstest.student.exception.StudentException
import org.example.msstest.student.repository.StudentRepository
import org.example.msstest.student.vo.StudentNo
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
                .orElseThrow { StudentException.NotFound(studentId) }
        return StudentResponse.from(student)
    }

    @Transactional(readOnly = true)
    fun getStudentByStudentNo(studentNo: String): StudentResponse {
        val student =
            studentRepository.findByStudentNo(StudentNo(studentNo))
                .orElseThrow { StudentException.NotFoundByNo(studentNo) }
        return StudentResponse.from(student)
    }

    @Transactional(readOnly = true)
    fun existsById(studentId: Long): Boolean = studentRepository.existsById(studentId)

    @Transactional(readOnly = true)
    fun findStudentEntityById(studentId: Long): Student =
        studentRepository.findById(studentId)
            .orElseThrow { StudentException.NotFound(studentId) }
}
