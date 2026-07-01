package com.gusgo.bbj.application.dtos.methodology

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CurriculumRequest(
    @field:NotBlank(message = "Name is required.")
    val name: String,

    @field:NotBlank(message = "Description is required.")
    val description: String,

    val modules: List<CurriculumModuleRequest>
)

data class CurriculumModuleRequest(
    @field:NotNull(message = "Module is required.")
    val id: UUID,

    val belts: List<CurriculumBeltRequest>
)

data class CurriculumBeltRequest(
    @field:NotNull(message = "Belt is required.")
    val id: UUID,

    val techniques: List<CurriculumTechniqueRequest>
)

data class CurriculumTechniqueRequest(
    @field:NotNull(message = "Technique is required.")
    val id: UUID,

    val required: Boolean = false,

    @field:Min(0) @field:Max(100)
    val minimumScore: Int = 0
)

data class CurriculumPatchRequest(
    @field:NotNull(message = "Active status is required.")
    val active: Boolean
)

data class CurriculumResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val active: Boolean,
    val modules: List<CurriculumModuleResponse>
)

data class CurriculumModuleResponse(
    val id: UUID,
    val name: String,
    val belts: List<CurriculumBeltResponse>
)

data class CurriculumBeltResponse(
    val id: UUID,
    val name: String,
    val color: String,
    val techniques: List<CurriculumTechniqueResponse>
)

data class CurriculumTechniqueResponse(
    val id: UUID,
    val name: String,
    val required: Boolean,
    val minimumScore: Int
)