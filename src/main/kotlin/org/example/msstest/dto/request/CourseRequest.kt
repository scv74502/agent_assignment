package org.example.msstest.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.example.msstest.domain.entity.CourseType

data class CreateCourseRequest(
    @field:NotBlank(message = "과목코드는 필수입니다")
    @field:Pattern(regexp = "^[A-Z]{2,4}[0-9]{3,4}$", message = "과목코드 형식이 올바르지 않습니다")
    val courseCode: String,
    @field:NotBlank(message = "과목명은 필수입니다")
    @field:Size(max = 100, message = "과목명은 100자 이하여야 합니다")
    val courseName: String,
    @field:NotNull(message = "학점은 필수입니다")
    @field:Min(value = 1, message = "학점은 1 이상이어야 합니다")
    @field:Max(value = 6, message = "학점은 6 이하여야 합니다")
    val credits: Int,
    @field:NotNull(message = "정원은 필수입니다")
    @field:Positive(message = "정원은 양수여야 합니다")
    val capacity: Int,
    @field:NotNull(message = "교수 ID는 필수입니다")
    @field:Positive(message = "교수 ID는 양수여야 합니다")
    val professorId: Long,
    @field:NotNull(message = "이수구분은 필수입니다")
    val courseType: CourseType,
    @field:NotBlank(message = "학과는 필수입니다")
    @field:Size(max = 50, message = "학과명은 50자 이하여야 합니다")
    val department: String,
)

data class UpdateCourseRequest(
    @field:Size(max = 100, message = "과목명은 100자 이하여야 합니다")
    val courseName: String?,
    @field:Min(value = 1, message = "학점은 1 이상이어야 합니다")
    @field:Max(value = 6, message = "학점은 6 이하여야 합니다")
    val credits: Int?,
    @field:Positive(message = "정원은 양수여야 합니다")
    val capacity: Int?,
)
