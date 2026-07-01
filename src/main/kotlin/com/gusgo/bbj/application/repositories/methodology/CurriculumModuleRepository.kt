package com.gusgo.bbj.application.repositories.methodology

import com.gusgo.bbj.domains.methodology.CurriculumModule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CurriculumModuleRepository : JpaRepository<CurriculumModule, UUID> {
    fun deleteByCurriculumId(id: UUID?)
}