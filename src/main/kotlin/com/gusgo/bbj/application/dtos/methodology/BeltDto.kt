package com.gusgo.bbj.application.dtos.methodology

import java.util.UUID

data class BeltResponse(
    val id: UUID,
    val name: String,
    val color: String,
)