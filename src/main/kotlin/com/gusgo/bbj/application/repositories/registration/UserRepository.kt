package com.gusgo.bbj.application.repositories.registration

import com.gusgo.bbj.domains.registration.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    fun findByIdAndAcademyId(id: UUID, academyId: UUID): User?

    fun findByEmail(email: String): User?

    fun existsByEmail(email: String): Boolean

    fun findAllByAcademyId(academyId: UUID): List<User>
}