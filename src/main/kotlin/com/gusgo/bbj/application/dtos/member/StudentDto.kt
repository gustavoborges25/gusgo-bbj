package com.gusgo.bbj.application.dtos.member

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.util.UUID

data class StudentCreateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    @field:NotNull(message = "Birth date is required.")
    val birthDate: LocalDate,

    @field:NotNull(message = "Belt ID is required.")
    val beltId: UUID,

    @field:Min(value = 0, message = "Belt degree cannot be negative.")
    val degree: Int = 0,

    val notes: String? = null
)

data class StudentUpdateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    @field:NotNull(message = "Birth date is required.")
    val birthDate: LocalDate,

    @field:NotNull(message = "Belt ID is required.")
    val beltId: UUID,

    @field:Min(value = 0, message = "Belt degree cannot be negative.")
    val degree: Int,

    @field:NotNull(message = "Active status is required.")
    val active: Boolean,

    val notes: String? = null
)

data class StudentPatchRequest(
    @field:NotNull(message = "Active status is required.")
    val active: Boolean,
)

data class StudentResponse(
    val id: UUID,
    val name: String,
    val birthDate: LocalDate,
    val beltId: UUID,
    val beltName: String,
    val beltColor: String,
    val degree: Int,
    val joinDate: LocalDate,
    val active: Boolean,
    val notes: String?
)