package org.example.msstest.initializer

import org.example.msstest.initializer.generator.CourseData
import org.example.msstest.initializer.generator.ProfessorData
import org.example.msstest.initializer.generator.ScheduleData
import org.example.msstest.initializer.generator.StudentData
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Timestamp
import java.time.LocalDateTime

@Component
class BatchInsertExecutor(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun insertProfessors(
        professors: List<ProfessorData>,
        batchSize: Int,
    ): Map<String, Long> {
        val now = Timestamp.valueOf(LocalDateTime.now())
        val professorNoToId = mutableMapOf<String, Long>()

        professors.chunked(batchSize).forEach { batch ->
            jdbcTemplate.batchUpdate(
                """
                INSERT INTO professors (professor_no, name, department, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?)
                """.trimIndent(),
                batch.map { professor ->
                    arrayOf(
                        professor.professorNo,
                        professor.name,
                        professor.department,
                        now,
                        now,
                    )
                },
            )
        }

        jdbcTemplate.query(
            "SELECT id, professor_no FROM professors",
        ) { rs, _ ->
            professorNoToId[rs.getString("professor_no")] = rs.getLong("id")
        }

        return professorNoToId
    }

    fun insertStudents(
        students: List<StudentData>,
        batchSize: Int,
    ) {
        val now = Timestamp.valueOf(LocalDateTime.now())

        students.chunked(batchSize).forEach { batch ->
            jdbcTemplate.batchUpdate(
                """
                INSERT INTO students (student_no, name, department, grade, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                batch.map { student ->
                    arrayOf(
                        student.studentNo,
                        student.name,
                        student.department,
                        student.grade,
                        now,
                        now,
                    )
                },
            )
        }
    }

    fun insertCourses(
        courses: List<CourseData>,
        batchSize: Int,
    ): Map<String, Long> {
        val now = Timestamp.valueOf(LocalDateTime.now())
        val courseCodeToId = mutableMapOf<String, Long>()

        courses.chunked(batchSize).forEach { batch ->
            jdbcTemplate.batchUpdate(
                """
                INSERT INTO courses (course_code, course_name, credits, capacity, current_enrollment, course_type, department, professor_id, version, created_at, updated_at)
                VALUES (?, ?, ?, ?, 0, ?, ?, ?, 0, ?, ?)
                """.trimIndent(),
                batch.map { course ->
                    arrayOf(
                        course.courseCode,
                        course.courseName,
                        course.credits,
                        course.capacity,
                        course.courseType,
                        course.department,
                        course.professorId,
                        now,
                        now,
                    )
                },
            )
        }

        jdbcTemplate.query(
            "SELECT id, course_code FROM courses",
        ) { rs, _ ->
            courseCodeToId[rs.getString("course_code")] = rs.getLong("id")
        }

        return courseCodeToId
    }

    fun insertSchedules(
        schedulesByCourseCode: Map<String, List<ScheduleData>>,
        courseCodeToId: Map<String, Long>,
        batchSize: Int,
    ) {
        val now = Timestamp.valueOf(LocalDateTime.now())
        val allSchedules = schedulesByCourseCode.flatMap { (courseCode, schedules) ->
            val courseId = courseCodeToId[courseCode] ?: return@flatMap emptyList()
            schedules.map { schedule -> courseId to schedule }
        }

        allSchedules.chunked(batchSize).forEach { batch ->
            jdbcTemplate.batchUpdate(
                """
                INSERT INTO course_schedules (course_id, day_of_week, start_time, end_time, location, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                batch.map { (courseId, schedule) ->
                    arrayOf(
                        courseId,
                        schedule.dayOfWeek.name,
                        schedule.startTime,
                        schedule.endTime,
                        schedule.location,
                        now,
                        now,
                    )
                },
            )
        }
    }
}
