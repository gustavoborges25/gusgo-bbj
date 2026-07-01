package com.gusgo.bbj.application.repositories.performance

import com.gusgo.bbj.domains.performance.EvaluationItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EvaluationItemRepository : JpaRepository<EvaluationItem, UUID> {
}