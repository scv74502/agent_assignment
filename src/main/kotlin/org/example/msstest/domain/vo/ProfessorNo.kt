package org.example.msstest.domain.vo

data class ProfessorNo(val value: String) {
    init {
        require(value.matches(PATTERN)) { "교수번호 형식이 올바르지 않습니다: $value" }
    }

    companion object {
        private val PATTERN = Regex("^[A-Z][0-9]{3,9}$")

        fun of(value: String): ProfessorNo = ProfessorNo(value)
    }
}
