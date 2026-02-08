package org.example.msstest.initializer.generator

import org.example.msstest.initializer.DataTokens
import kotlin.random.Random

data class ProfessorData(
    val professorNo: String,
    val name: String,
    val department: String,
)

class ProfessorGenerator(
    private val random: Random = Random.Default,
) {
    private var professorSequence = 0

    fun generate(count: Int): List<ProfessorData> {
        val professors = mutableListOf<ProfessorData>()
        val professorsPerDept = count / DataTokens.DEPARTMENTS.size
        val remainder = count % DataTokens.DEPARTMENTS.size

        DataTokens.DEPARTMENTS.forEachIndexed { index, department ->
            val deptCount = professorsPerDept + if (index < remainder) 1 else 0
            repeat(deptCount) {
                professors.add(generateProfessor(department))
            }
        }

        return professors
    }

    private fun generateProfessor(department: String): ProfessorData {
        professorSequence++
        val professorNo = "P${professorSequence.toString().padStart(4, '0')}"
        val name = generateName()

        return ProfessorData(
            professorNo = professorNo,
            name = name,
            department = department,
        )
    }

    private fun generateName(): String {
        val lastName = DataTokens.LAST_NAMES[random.nextInt(DataTokens.LAST_NAMES.size)]
        val firstName1 = DataTokens.FIRST_NAME_CHARS[random.nextInt(DataTokens.FIRST_NAME_CHARS.size)]
        val firstName2 = DataTokens.FIRST_NAME_CHARS[random.nextInt(DataTokens.FIRST_NAME_CHARS.size)]
        return "$lastName$firstName1$firstName2"
    }
}
