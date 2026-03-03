package org.example.msstest.exception

import org.example.msstest.dto.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

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

    @ExceptionHandler(StudentException::class)
    fun handleStudentException(e: StudentException): ResponseEntity<ErrorResponse> {
        logger.warn("StudentException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse.of(e.errorCode, e.message))
    }

    @ExceptionHandler(CourseException::class)
    fun handleCourseException(e: CourseException): ResponseEntity<ErrorResponse> {
        logger.warn("CourseException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse.of(e.errorCode, e.message))
    }

    @ExceptionHandler(ProfessorException::class)
    fun handleProfessorException(e: ProfessorException): ResponseEntity<ErrorResponse> {
        logger.warn("ProfessorException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse.of(e.errorCode, e.message))
    }

    @ExceptionHandler(LockException::class)
    fun handleLockException(e: LockException): ResponseEntity<ErrorResponse> {
        logger.warn("LockException: ${e.errorCode.code} - ${e.message}")
        return ResponseEntity
            .status(e.errorCode.status)
            .body(ErrorResponse.of(e.errorCode, e.message))
    }

    @ExceptionHandler(QueueException::class)
    fun handleQueueException(e: QueueException): ResponseEntity<ErrorResponse> {
        logger.warn("QueueException: ${e.errorCode.code} - ${e.message}")
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

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val message = "잘못된 파라미터입니다: ${e.name}=${e.value}"
        logger.warn("TypeMismatchException: $message")
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
