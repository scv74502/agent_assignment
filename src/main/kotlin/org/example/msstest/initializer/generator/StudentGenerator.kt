package org.example.msstest.initializer.generator

import org.example.msstest.initializer.DataTokens
import kotlin.random.Random

data class StudentData(
    val studentNo: String,
    val name: String,
    val department: String,
    val grade: Int,
)

class StudentGenerator(
    private val random: Random = Random.Default,
) {
    private var studentSequence = 0

    fun generate(count: Int): List<StudentData> {
        val students = mutableListOf<StudentData>()
        val studentsPerDept = count / DataTokens.DEPARTMENTS.size
        val remainder = count % DataTokens.DEPARTMENTS.size

        DataTokens.DEPARTMENTS.forEachIndexed { index, department ->
            val deptCount = studentsPerDept + if (index < remainder) 1 else 0
            repeat(deptCount) {
                students.add(generateStudent(department))
            }
        }

        return students
    }

    private fun generateStudent(department: String): StudentData {
        studentSequence++
        val year = 2021 + random.nextInt(4)
        val grade = 2025 - year + 1
        val sequenceStr = studentSequence.toString().padStart(5, '0')
        val studentNo = "$year$sequenceStr"
        val name = generateName()

        return StudentData(
            studentNo = studentNo,
            name = name,
            department = department,
            grade = grade.coerceIn(1, 4),
        )
    }

    private fun generateName(): String {
        val lastName = DataTokens.LAST_NAMES[random.nextInt(DataTokens.LAST_NAMES.size)]
        val firstName1 = DataTokens.FIRST_NAME_CHARS[random.nextInt(DataTokens.FIRST_NAME_CHARS.size)]
        val firstName2 = DataTokens.FIRST_NAME_CHARS[random.nextInt(DataTokens.FIRST_NAME_CHARS.size)]
        return "$lastName$firstName1$firstName2"
    }
}
