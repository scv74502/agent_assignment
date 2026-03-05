package org.example.msstest.domain.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.example.msstest.common.entity.BaseEntity
import org.example.msstest.domain.vo.CourseCode
import org.example.msstest.professor.entity.Professor
import org.example.msstest.domain.vo.Credits
import org.example.msstest.domain.vo.converter.CourseCodeConverter
import org.example.msstest.domain.vo.converter.CreditsConverter

@Entity
@Table(name = "courses")
class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true, length = 10)
    @Convert(converter = CourseCodeConverter::class)
    val courseCode: CourseCode,
    @Column(nullable = false, length = 100)
    val courseName: String,
    @Column(nullable = false)
    @Convert(converter = CreditsConverter::class)
    val credits: Credits,
    @Column(nullable = false)
    val capacity: Int,
    @Column(nullable = false)
    var currentEnrollment: Int = 0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val courseType: CourseType,
    @Column(nullable = false, length = 50)
    val department: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    val professor: Professor,
    @OneToMany(mappedBy = "course", cascade = [CascadeType.ALL], orphanRemoval = true)
    val schedules: MutableList<CourseSchedule> = mutableListOf(),
    @Version
    val version: Long = 0,
) : BaseEntity() {
    val remainingCapacity: Int
        get() = capacity - currentEnrollment

    fun isFull(): Boolean = currentEnrollment >= capacity

    fun incrementEnrollment() {
        require(!isFull()) { "강좌 정원이 초과되었습니다" }
        currentEnrollment++
    }

    fun decrementEnrollment() {
        require(currentEnrollment > 0) { "수강 인원이 0명입니다" }
        currentEnrollment--
    }

    fun addSchedule(schedule: CourseSchedule) {
        schedules.add(schedule)
    }

    companion object {
        fun create(
            courseCode: String,
            courseName: String,
            credits: Int,
            capacity: Int,
            professor: Professor,
            courseType: CourseType,
            department: String,
        ): Course =
            Course(
                courseCode = CourseCode(courseCode),
                courseName = courseName,
                credits = Credits(credits),
                capacity = capacity,
                courseType = courseType,
                department = department,
                professor = professor,
            )
    }
}
