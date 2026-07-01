package com.gusgo.bbj.application.dtos.methodology

import java.util.UUID

data class ModuleResponse (
    val id: UUID,
    val name: String,
    val description: String
)