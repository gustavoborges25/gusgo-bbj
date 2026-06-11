package com.gusgo.bbj.application.repositories.core

import com.gusgo.bbj.domains.core.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findAllByAcademyId(academyId: UUID): List<User>
}