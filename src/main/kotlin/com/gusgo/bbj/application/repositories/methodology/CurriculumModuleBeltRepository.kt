package com.gusgo.bbj.application.repositories.methodology

import com.gusgo.bbj.domains.methodology.CurriculumModuleBelt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CurriculumModuleBeltRepository : JpaRepository<CurriculumModuleBelt, UUID> {
}