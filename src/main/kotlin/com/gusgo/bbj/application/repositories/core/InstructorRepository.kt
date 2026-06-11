package com.gusgo.bbj.application.repositories.core

import com.gusgo.bbj.domains.core.Instructor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InstructorRepository: JpaRepository<Instructor, UUID> {
    fun findByUserId(userId: UUID): Instructor?

    fun findAllByAcademyIdAndActiveTrue(academyId: UUID): List<Instructor>

    fun existsByUserId(userId: UUID): Boolean
}