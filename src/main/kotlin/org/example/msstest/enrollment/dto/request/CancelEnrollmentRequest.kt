package org.example.msstest.enrollment.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CancelEnrollmentRequest(
    @field:NotNull(message = "학생 ID는 필수입니다")
    @field:Positive(message = "학생 ID는 양수여야 합니다")
    val studentId: Long,
    @field:NotNull(message = "강좌 ID는 필수입니다")
    @field:Positive(message = "강좌 ID는 양수여야 합니다")
    val courseId: Long,
)
