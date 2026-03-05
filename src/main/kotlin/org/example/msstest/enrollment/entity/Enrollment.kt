package org.example.msstest.enrollment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.example.msstest.common.entity.BaseEntity
import org.example.msstest.course.entity.Course
import org.example.msstest.student.entity.Student

@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["student_id", "course_id"]),
    ],
)
class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: Course,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: EnrollmentStatus = EnrollmentStatus.ENROLLED,
) : BaseEntity() {
    fun cancel() {
        require(status == EnrollmentStatus.ENROLLED) { "이미 취소된 수강신청입니다" }
        status = EnrollmentStatus.CANCELLED
    }

    companion object {
        fun create(
            student: Student,
            course: Course,
        ): Enrollment = Enrollment(student = student, course = course)
    }
}
