package com.gusgo.bbj.application.services.methodology

import com.gusgo.bbj.application.dtos.methodology.BeltResponse
import com.gusgo.bbj.application.dtos.methodology.ModuleResponse
import com.gusgo.bbj.application.repositories.methodology.ModuleRepository
import com.gusgo.bbj.domains.methodology.Belt
import com.gusgo.bbj.domains.methodology.Module
import org.springframework.stereotype.Service

@Service
class ModuleService(
    private val moduleRepository: ModuleRepository
) {
    fun findAll() : List<ModuleResponse> {
        val modules = moduleRepository.findAllByOrderByOrderPositionAsc()
        return modules.map { it.toResponse() }
    }

    private fun Module.toResponse() = ModuleResponse(
        id = this.id!!,
        name = this.name,
        description = this.description
    )

}