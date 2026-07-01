package com.gusgo.bbj.application.repositories.performance

import com.gusgo.bbj.domains.performance.Evaluation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EvaluationRepository : JpaRepository<Evaluation, UUID> {
}