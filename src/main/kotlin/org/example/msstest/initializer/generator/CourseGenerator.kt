package org.example.msstest.initializer.generator

import org.example.msstest.initializer.DataTokens
import kotlin.random.Random

data class CourseData(
    val courseCode: String,
    val courseName: String,
    val credits: Int,
    val capacity: Int,
    val professorId: Long,
    val courseType: String,
    val department: String,
    val schedules: List<ScheduleData>,
)

class CourseGenerator(
    private val scheduleGenerator: ScheduleGenerator = ScheduleGenerator(),
    private val random: Random = Random.Default,
) {
    private val generalDepartments = setOf("국어국문학과", "영어영문학과", "수학과", "물리학과")
    private val courseCountByDept = mutableMapOf<String, Int>()

    private fun determineCourseType(department: String): String =
        if (department in generalDepartments) {
            if (random.nextFloat() < 0.3f) "GENERAL_REQUIRED" else "GENERAL_ELECTIVE"
        } else {
            if (random.nextFloat() < 0.4f) "MAJOR_REQUIRED" else "MAJOR_ELECTIVE"
        }

    fun generate(
        count: Int,
        professorsByDept: Map<String, List<Long>>,
    ): List<CourseData> {
        val courses = mutableListOf<CourseData>()
        val coursesPerDept = count / DataTokens.DEPARTMENTS.size
        val remainder = count % DataTokens.DEPARTMENTS.size

        DataTokens.DEPARTMENTS.forEachIndexed { index, department ->
            val deptCount = coursesPerDept + if (index < remainder) 1 else 0
            val deptProfessors = professorsByDept[department] ?: emptyList()
            if (deptProfessors.isEmpty()) return@forEachIndexed

            repeat(deptCount) {
                courses.add(generateCourse(department, deptProfessors))
            }
        }

        return courses
    }

    private fun generateCourse(
        department: String,
        professorIds: List<Long>,
    ): CourseData {
        val deptCourseNum = courseCountByDept.getOrDefault(department, 0) + 1
        courseCountByDept[department] = deptCourseNum

        val prefix = DataTokens.COURSE_PREFIXES[department] ?: "XX"
        val courseCode = "$prefix${(100 + deptCourseNum).toString().padStart(3, '0')}"

        val courseTypes = DataTokens.COURSE_TYPES[department] ?: listOf("기초")
        val baseCourseName = courseTypes[random.nextInt(courseTypes.size)]
        val level = when {
            deptCourseNum <= 10 -> "기초"
            deptCourseNum <= 25 -> "중급"
            else -> "고급"
        }
        val courseName = "$baseCourseName $level ${(deptCourseNum % 5) + 1}"

        val credits = listOf(2, 3, 3, 3, 4)[random.nextInt(5)]
        val capacity = listOf(30, 40, 50, 60, 80, 100)[random.nextInt(6)]
        val professorId = professorIds[random.nextInt(professorIds.size)]
        val schedules = scheduleGenerator.generateForCredits(credits)
        val courseType = determineCourseType(department)

        return CourseData(
            courseCode = courseCode,
            courseName = courseName,
            credits = credits,
            capacity = capacity,
            professorId = professorId,
            courseType = courseType,
            department = department,
            schedules = schedules,
        )
    }
}
