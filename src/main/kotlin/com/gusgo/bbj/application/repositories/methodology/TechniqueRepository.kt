package com.gusgo.bbj.application.repositories.methodology

import com.gusgo.bbj.domains.methodology.Technique
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TechniqueRepository: JpaRepository<Technique, UUID> {
    fun findByIdAndAcademyId(id: UUID, academyId: UUID): Technique?

    fun findAllByAcademyId(academyId: UUID): List<Technique>
}