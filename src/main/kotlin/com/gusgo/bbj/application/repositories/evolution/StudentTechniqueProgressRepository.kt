package com.gusgo.bbj.application.repositories.evolution

import com.gusgo.bbj.domains.evolution.StudentTechniqueProgress
import com.gusgo.bbj.domains.evolution.TechniqueProgressStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentTechniqueProgressRepository : JpaRepository<StudentTechniqueProgress, UUID> {
    fun findByStudentIdAndTechniqueId(studentId: UUID, techniqueId: UUID): StudentTechniqueProgress?

    fun findAllByStudentId(studentId: UUID): List<StudentTechniqueProgress>

    fun findAllByStudentIdAndStatus(studentId: UUID, status: TechniqueProgressStatus): List<StudentTechniqueProgress>

    fun countByStudentIdAndTechniqueModuleIdAndStatus(studentId: UUID, moduleId: UUID, status: TechniqueProgressStatus): Long
}