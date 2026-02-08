package org.example.msstest.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.msstest.domain.vo.ProfessorNo
import org.example.msstest.domain.vo.converter.ProfessorNoConverter

@Entity
@Table(name = "professors")
class Professor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true, length = 10)
    @Convert(converter = ProfessorNoConverter::class)
    val professorNo: ProfessorNo,
    @Column(nullable = false, length = 50)
    val name: String,
    @Column(nullable = false, length = 50)
    val department: String,
) : BaseEntity() {
    companion object {
        fun create(
            professorNo: String,
            name: String,
            department: String,
        ): Professor = Professor(
            professorNo = ProfessorNo(professorNo),
            name = name,
            department = department,
        )
    }
}
