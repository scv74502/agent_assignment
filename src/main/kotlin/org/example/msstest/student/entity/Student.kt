package org.example.msstest.student.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.msstest.common.entity.BaseEntity
import org.example.msstest.student.vo.StudentNo
import org.example.msstest.student.vo.converter.StudentNoConverter

@Entity
@Table(name = "students")
class Student(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true, length = 10)
    @Convert(converter = StudentNoConverter::class)
    val studentNo: StudentNo,
    @Column(nullable = false, length = 50)
    val name: String,
    @Column(nullable = false, length = 50)
    val department: String,
    @Column(nullable = false)
    val grade: Int,
) : BaseEntity() {
    companion object {
        fun create(
            studentNo: String,
            name: String,
            department: String,
            grade: Int,
        ): Student = Student(
            studentNo = StudentNo(studentNo),
            name = name,
            department = department,
            grade = grade,
        )
    }
}
