package org.example.msstest.course.vo

data class Credits(val value: Int) {
    init {
        require(value in 1..6) { "학점은 1~6 사이여야 합니다: $value" }
    }

    operator fun plus(other: Credits): Credits = Credits(this.value + other.value)

    operator fun compareTo(other: Int): Int = this.value.compareTo(other)

    companion object {
        fun of(value: Int): Credits = Credits(value)
    }
}
