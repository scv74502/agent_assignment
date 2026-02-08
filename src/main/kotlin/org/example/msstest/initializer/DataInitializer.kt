package org.example.msstest.initializer

import org.example.msstest.initializer.generator.CourseGenerator
import org.example.msstest.initializer.generator.ProfessorGenerator
import org.example.msstest.initializer.generator.StudentGenerator
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
@Profile("local", "dev")
@EnableConfigurationProperties(InitializerProperties::class)
class DataInitializer(
    private val properties: InitializerProperties,
    private val batchInsertExecutor: BatchInsertExecutor,
    private val jdbcTemplate: JdbcTemplate,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(args: ApplicationArguments) {
        if (!properties.enabled) {
            log.info("Data initializer is disabled")
            return
        }

        if (hasExistingData()) {
            log.info("Data already exists, skipping initialization")
            return
        }

        log.info("Starting data initialization...")
        val totalTime = measureTimeMillis {
            initializeData()
        }
        log.info("Data initialization completed in ${totalTime}ms")
    }

    private fun hasExistingData(): Boolean {
        val professorCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM professors",
            Int::class.java,
        ) ?: 0
        return professorCount > 0
    }

    private fun initializeData() {
        val professorGenerator = ProfessorGenerator()
        val studentGenerator = StudentGenerator()
        val courseGenerator = CourseGenerator()

        val professorTime = measureTimeMillis {
            val professors = professorGenerator.generate(properties.professors)
            val professorNoToId = batchInsertExecutor.insertProfessors(professors, properties.batchSize)
            log.info("Inserted ${professors.size} professors")

            val professorsByDept = professors.groupBy { it.department }
                .mapValues { (_, profs) -> profs.mapNotNull { professorNoToId[it.professorNo] } }

            val courseTime = measureTimeMillis {
                val courses = courseGenerator.generate(properties.courses, professorsByDept)
                val courseCodeToId = batchInsertExecutor.insertCourses(courses, properties.batchSize)
                log.info("Inserted ${courses.size} courses")

                val schedulesByCourseCode = courses.associate { it.courseCode to it.schedules }
                batchInsertExecutor.insertSchedules(schedulesByCourseCode, courseCodeToId, properties.batchSize)
                val totalSchedules = schedulesByCourseCode.values.sumOf { it.size }
                log.info("Inserted $totalSchedules course schedules")
            }
            log.info("Courses and schedules inserted in ${courseTime}ms")
        }
        log.info("Professors inserted in ${professorTime}ms")

        val studentTime = measureTimeMillis {
            val students = studentGenerator.generate(properties.students)
            batchInsertExecutor.insertStudents(students, properties.batchSize)
            log.info("Inserted ${students.size} students")
        }
        log.info("Students inserted in ${studentTime}ms")
    }
}
