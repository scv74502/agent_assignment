package org.example.msstest.domain.vo

@JvmInline
value class CourseCode(val value: String) {
    init {
        require(value.matches(PATTERN)) { "과목코드 형식이 올바르지 않습니다: $value" }
    }

    companion object {
        private val PATTERN = Regex("^[A-Z]{2,4}[0-9]{3,4}$")

        fun of(value: String): CourseCode = CourseCode(value)
    }
}
