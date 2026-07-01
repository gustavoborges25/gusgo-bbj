package com.gusgo.bbj.application.repositories.graduation

import com.gusgo.bbj.domains.graduation.BeltPromotion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BeltPromotionRepository : JpaRepository<BeltPromotion, UUID> {
}