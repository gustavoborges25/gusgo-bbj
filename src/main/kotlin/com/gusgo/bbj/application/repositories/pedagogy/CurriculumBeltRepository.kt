package com.gusgo.bbj.application.repositories.pedagogy

import com.gusgo.bbj.domains.pedagogy.CurriculumBelt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CurriculumBeltRepository : JpaRepository<CurriculumBelt, UUID> {

    fun findAllByCurriculumIdOrderByOrderPositionAsc(curriculumId: UUID): List<CurriculumBelt>

    fun existsByCurriculumIdAndBeltId(curriculumId: UUID, beltId: UUID): Boolean
}