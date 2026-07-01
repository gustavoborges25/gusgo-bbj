package com.gusgo.bbj.application.dtos.methodology

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class TechniqueCreateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    val description: String,

    val videoUrl: String?,

)

data class TechniqueUpdateRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    val description: String,

    val videoUrl: String?,

    @field:NotNull(message = "Active status is required.")
    val active: Boolean
)

data class TechniquePatchRequest(
    @field:NotNull(message = "Active status is required.")
    val active: Boolean
)

data class TechniqueResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val videoUrl: String?,
    val active: Boolean
)