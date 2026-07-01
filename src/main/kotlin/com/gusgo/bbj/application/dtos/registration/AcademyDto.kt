package com.gusgo.bbj.application.dtos.registration

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class AcademyRequest(
    @field:NotBlank(message = "Name is mandatory.")
    val name: String,

    @field:NotBlank(message = "Email is mandatory")
    @field:Email(message = "Invalid email")
    val email: String,

    @field:NotBlank(message = "Phone is mandatory")
    val phone: String
)

data class AcademyResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val phone: String
)