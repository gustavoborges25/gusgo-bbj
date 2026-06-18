package com.gusgo.bbj.application.dtos.pedagogy

import java.time.LocalDate
import java.util.UUID

data class BeltResponse(
    val id: UUID,
    val name: String,
    val color: String,
)