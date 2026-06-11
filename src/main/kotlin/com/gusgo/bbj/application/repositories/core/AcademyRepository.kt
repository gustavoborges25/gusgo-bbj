package com.gusgo.bbj.application.repositories.core

import com.gusgo.bbj.domains.core.Academy
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AcademyRepository: JpaRepository<Academy, UUID> {
    fun existsByEmail(email: String): Boolean
}