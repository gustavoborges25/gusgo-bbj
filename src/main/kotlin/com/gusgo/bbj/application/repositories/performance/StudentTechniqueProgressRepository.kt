package com.gusgo.bbj.application.repositories.performance

import com.gusgo.bbj.domains.performance.StudentTechniqueProgress
import com.gusgo.bbj.domains.performance.TechniqueProgressStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentTechniqueProgressRepository : JpaRepository<StudentTechniqueProgress, UUID> {
}