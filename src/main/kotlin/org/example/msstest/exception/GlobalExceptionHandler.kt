package org.example.msstest.exception

import org.example.msstest.dto.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(EnrollmentException::class)
    fun handleEnrollmentException(e: EnrollmentException): ResponseEntity<ErrorResponse> {
        logger.warn("EnrollmentException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse.of(e.errorCode, e.message))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message =
            e.bindingResult.fieldErrors
                .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        logger.warn("ValidationException: $message")
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, message))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException: ${e.message}")
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse.of(ErrorCode.INVALID_INPUT, e.message ?: "잘못된 입력입니다"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected exception", e)
        return ResponseEntity
            .internalServerError()
            .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR))
    }
}
