package org.example.msstest.common.dto

import org.example.msstest.common.exception.ErrorCode
import java.time.LocalDateTime

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun of(errorCode: ErrorCode): ErrorResponse =
            ErrorResponse(
                code = errorCode.code,
                message = errorCode.message,
            )

        fun of(
            errorCode: ErrorCode,
            message: String,
        ): ErrorResponse =
            ErrorResponse(
                code = errorCode.code,
                message = message,
            )
    }
}
