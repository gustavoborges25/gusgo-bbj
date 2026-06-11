package com.gusgo.bbj.application.repositories.evolution

import com.gusgo.bbj.domains.evolution.BeltPromotion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BeltPromotionRepository : JpaRepository<BeltPromotion, UUID> {
    fun findAllByStudentIdOrderByPromotionDateDescCreatedAtDesc(studentId: UUID): List<BeltPromotion>

    fun findFirstByStudentIdOrderByPromotionDateDescCreatedAtDesc(studentId: UUID): BeltPromotion?
}