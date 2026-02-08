package org.example.msstest.domain.entity

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
import java.time.DayOfWeek
import java.time.LocalTime

@Entity
@Table(name = "course_schedules")
class CourseSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: Course,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val dayOfWeek: DayOfWeek,
    @Column(nullable = false)
    val startTime: LocalTime,
    @Column(nullable = false)
    val endTime: LocalTime,
    @Column(nullable = false, length = 50)
    val location: String,
) : BaseEntity() {
    fun overlaps(other: CourseSchedule): Boolean {
        if (this.dayOfWeek != other.dayOfWeek) return false
        return this.startTime < other.endTime && other.startTime < this.endTime
    }

    companion object {
        fun create(
            course: Course,
            dayOfWeek: DayOfWeek,
            startTime: LocalTime,
            endTime: LocalTime,
            location: String,
        ): CourseSchedule =
            CourseSchedule(
                course = course,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime,
                location = location,
            )
    }
}
