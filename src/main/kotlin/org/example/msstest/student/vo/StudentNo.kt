package org.example.msstest.student.vo

data class StudentNo(val value: String) {
    init {
        require(value.matches(PATTERN)) { "학번 형식이 올바르지 않습니다: $value" }
    }

    companion object {
        private val PATTERN = Regex("^[0-9]{8,10}$")

        fun of(value: String): StudentNo = StudentNo(value)
    }
}
