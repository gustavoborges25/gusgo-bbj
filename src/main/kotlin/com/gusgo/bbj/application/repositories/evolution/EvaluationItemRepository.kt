package com.gusgo.bbj.application.repositories.evolution

import com.gusgo.bbj.domains.evolution.EvaluationItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EvaluationItemRepository : JpaRepository<EvaluationItem, UUID> {
    fun findAllByEvaluationId(evaluationId: UUID): List<EvaluationItem>

    fun existsByEvaluationIdAndTechniqueId(evaluationId: UUID, techniqueId: UUID): Boolean
}