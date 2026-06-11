package com.gusgo.bbj.application.repositories.pedagogy

import com.gusgo.bbj.domains.pedagogy.Technique
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TechniqueRepository: JpaRepository<Technique, UUID> {
    fun findAllByModuleIdAndActiveTrueOrderByOrderPositionAsc(moduleId: UUID): List<Technique>

    fun findAllByNameContainingIgnoreCaseAndActiveTrue(name: String): List<Technique>

    fun existsByModuleIdAndOrderPosition(moduleId: UUID, orderPosition: Int): Boolean
}