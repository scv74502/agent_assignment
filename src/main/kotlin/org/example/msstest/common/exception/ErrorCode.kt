package org.example.msstest.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력입니다"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다"),

    // Student
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "학생을 찾을 수 없습니다"),
    DUPLICATE_STUDENT(HttpStatus.CONFLICT, "S002", "이미 존재하는 학번입니다"),

    // Professor
    PROFESSOR_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "교수를 찾을 수 없습니다"),
    DUPLICATE_PROFESSOR(HttpStatus.CONFLICT, "P002", "이미 존재하는 교수번호입니다"),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "CR001", "강좌를 찾을 수 없습니다"),
    COURSE_FULL(HttpStatus.CONFLICT, "CR002", "강좌 정원이 초과되었습니다"),
    DUPLICATE_COURSE(HttpStatus.CONFLICT, "CR003", "이미 존재하는 과목코드입니다"),

    // Enrollment
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "E001", "이미 수강신청한 강좌입니다"),
    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E002", "수강신청 내역을 찾을 수 없습니다"),
    CREDIT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "E003", "최대 학점을 초과했습니다"),
    SCHEDULE_CONFLICT(HttpStatus.CONFLICT, "E004", "시간표가 중복됩니다"),
    ALREADY_CANCELLED(HttpStatus.CONFLICT, "E005", "이미 취소된 수강신청입니다"),

    // Queue
    QUEUE_FULL(HttpStatus.SERVICE_UNAVAILABLE, "Q001", "대기열이 가득 찼습니다"),
    QUEUE_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Q002", "대기 시간이 초과되었습니다"),

    // Lock
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "L001", "락 획득에 실패했습니다"),
}
