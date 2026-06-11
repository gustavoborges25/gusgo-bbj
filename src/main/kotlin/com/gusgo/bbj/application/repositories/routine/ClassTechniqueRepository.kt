package com.gusgo.bbj.application.repositories.routine

import com.gusgo.bbj.domains.routine.ClassTechnique
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClassTechniqueRepository : JpaRepository<ClassTechnique, UUID> {
    fun findAllByClassRefId(classId: UUID): List<ClassTechnique>

    fun existsByClassRefIdAndTechniqueId(classId: UUID, techniqueId: UUID): Boolean
}