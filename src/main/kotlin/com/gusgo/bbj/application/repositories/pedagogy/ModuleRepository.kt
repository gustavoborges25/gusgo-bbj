package com.gusgo.bbj.application.repositories.pedagogy

import com.gusgo.bbj.domains.pedagogy.Module
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ModuleRepository: JpaRepository<Module, UUID> {
    fun findAllByCurriculumBeltIdOrderByOrderPositionAsc(curriculumBeltId: UUID): List<Module>

    fun existsByCurriculumBeltIdAndOrderPosition(curriculumBeltId: UUID, orderPosition: Int): Boolean
}