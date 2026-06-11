package com.gusgo.bbj.application.repositories.pedagogy

import com.gusgo.bbj.domains.pedagogy.Curriculum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CurriculumRepository : JpaRepository<Curriculum, UUID> {
    fun findAllByAcademyId(academyId: UUID): List<Curriculum>

    fun findAllByAcademyIdAndActiveTrue(academyId: UUID): List<Curriculum>
}