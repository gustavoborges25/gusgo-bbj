package com.gusgo.bbj.application.services.methodology

import com.gusgo.bbj.application.dtos.methodology.BeltResponse
import com.gusgo.bbj.application.repositories.methodology.BeltRepository
import com.gusgo.bbj.domains.methodology.Belt
import org.springframework.stereotype.Service

@Service
class BeltService(
    private val beltRepository: BeltRepository
) {
    fun findAll() : List<BeltResponse> {
        val belts = beltRepository.findAllByOrderByOrderPositionAsc()
        return belts.map { it.toResponse() }
    }

    private fun Belt.toResponse() = BeltResponse(
        id = this.id!!,
        name = this.name,
        color = this.color
    )
}