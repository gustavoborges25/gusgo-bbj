package com.gusgo.bbj.application.repositories.pedagogy

import com.gusgo.bbj.domains.pedagogy.Belt
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BeltRepository : JpaRepository<Belt, UUID> {
    fun findAllByOrderByOrderPositionAsc(): List<Belt>

    fun findByName(name: String): Belt?
}