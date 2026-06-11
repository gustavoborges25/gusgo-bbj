package com.gusgo.bbj.application.repositories.member

import com.gusgo.bbj.domains.member.Student
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StudentRepository: JpaRepository<Student, UUID> {
    fun findByUserId(userId: UUID): Student?

    fun findAllByAcademyId(academyId: UUID): List<Student>

    fun findAllByAcademyIdAndActive(academyId: UUID, active: Boolean): List<Student>

    fun findAllByAcademyIdAndNameContainingIgnoreCase(academyId: UUID, name: String): List<Student>

    fun existsByUserId(userId: UUID): Boolean
}