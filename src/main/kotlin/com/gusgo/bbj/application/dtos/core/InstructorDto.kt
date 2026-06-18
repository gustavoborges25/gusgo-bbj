package com.gusgo.bbj.application.dtos.core

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class InstructorCreateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Invalid email format.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    val password: String,

    @field:NotNull(message = "Belt ID is required.")
    val beltId: UUID,

    @field:Min(value = 0, message = "Belt degree cannot be negative.")
    val degree: Int = 0
)

data class InstructorUpdateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    val password: String?,

    @field:NotNull(message = "Belt ID is required.")
    val beltId: UUID,

    @field:Min(value = 0, message = "Belt degree cannot be negative.")
    val degree: Int,

    @field:NotNull(message = "Active status is required.")
    val active: Boolean
)

data class InstructorPatchRequest(
    @field:NotNull(message = "Active status is required.")
    val active: Boolean
)

data class InstructorResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val beltId: UUID,
    val beltName: String,
    val beltColor: String,
    val degree: Int,
    val active: Boolean
)