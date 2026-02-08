package org.example.msstest.dto.response

import org.example.msstest.domain.entity.Student

data class StudentResponse(
    val id: Long,
    val studentNo: String,
    val name: String,
    val department: String,
    val grade: Int,
) {
    companion object {
        fun from(student: Student): StudentResponse =
            StudentResponse(
                id = student.id,
                studentNo = student.studentNo.value,
                name = student.name,
                department = student.department,
                grade = student.grade,
            )
    }
}
