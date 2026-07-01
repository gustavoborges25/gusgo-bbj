package com.gusgo.bbj.application.repositories.methodology

import com.gusgo.bbj.domains.methodology.Module
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ModuleRepository: JpaRepository<Module, UUID> {
    fun findAllByOrderByOrderPositionAsc() : List<Module>
}