package com.gusgo.bbj.application.services.pedagogy

import com.gusgo.bbj.application.dtos.pedagogy.BeltResponse
import com.gusgo.bbj.application.repositories.pedagogy.BeltRepository
import com.gusgo.bbj.domains.pedagogy.Belt
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