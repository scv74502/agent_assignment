package org.example.msstest.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateProfessorRequest(
    @field:NotBlank(message = "교수번호는 필수입니다")
    @field:Pattern(regexp = "^[A-Z][0-9]{3,9}$", message = "교수번호 형식이 올바르지 않습니다")
    val professorNo: String,
    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 2, max = 50, message = "이름은 2~50자여야 합니다")
    val name: String,
    @field:NotBlank(message = "학과는 필수입니다")
    @field:Size(max = 50, message = "학과명은 50자 이하여야 합니다")
    val department: String,
)

data class UpdateProfessorRequest(
    @field:Size(min = 2, max = 50, message = "이름은 2~50자여야 합니다")
    val name: String?,
    @field:Size(max = 50, message = "학과명은 50자 이하여야 합니다")
    val department: String?,
)
