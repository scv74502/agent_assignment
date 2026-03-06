package org.example.msstest.course.vo

import org.example.msstest.course.constants.CourseConstants

data class Credits(val value: Int) {
    init {
        require(value in 1..6) { "${CourseConstants.INVALID_CREDITS_MESSAGE}: $value" }
    }

    operator fun plus(other: Credits): Credits = Credits(this.value + other.value)

    operator fun compareTo(other: Int): Int = this.value.compareTo(other)

    companion object {
        fun of(value: Int): Credits = Credits(value)
    }
}
