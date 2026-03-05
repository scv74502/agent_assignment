package org.example.msstest.student.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateStudentRequest(
    @field:NotBlank(message = "학번은 필수입니다")
    @field:Pattern(regexp = "^[0-9]{8,10}$", message = "학번은 8~10자리 숫자여야 합니다")
    val studentNo: String,
    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 2, max = 50, message = "이름은 2~50자여야 합니다")
    val name: String,
    @field:NotBlank(message = "학과는 필수입니다")
    @field:Size(max = 50, message = "학과명은 50자 이하여야 합니다")
    val department: String,
    @field:Min(value = 1, message = "학년은 1 이상이어야 합니다")
    @field:Max(value = 6, message = "학년은 6 이하여야 합니다")
    val grade: Int,
)

data class UpdateStudentRequest(
    @field:Size(min = 2, max = 50, message = "이름은 2~50자여야 합니다")
    val name: String?,
    @field:Size(max = 50, message = "학과명은 50자 이하여야 합니다")
    val department: String?,
    @field:Min(value = 1, message = "학년은 1 이상이어야 합니다")
    @field:Max(value = 6, message = "학년은 6 이하여야 합니다")
    val grade: Int?,
)
