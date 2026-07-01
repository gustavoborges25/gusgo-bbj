package com.gusgo.bbj.application.repositories.registration

import com.gusgo.bbj.domains.registration.Academy
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AcademyRepository: JpaRepository<Academy, UUID> {
    fun existsByEmail(email: String): Boolean
}