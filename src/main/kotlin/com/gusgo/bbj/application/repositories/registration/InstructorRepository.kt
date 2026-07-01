package com.gusgo.bbj.application.repositories.registration

import com.gusgo.bbj.domains.registration.Instructor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface InstructorRepository: JpaRepository<Instructor, UUID> {
    fun findAllByAcademyId(academyId: UUID): MutableList<Instructor>

    fun findByIdAndAcademyId(id: UUID, academyId: UUID): Instructor?
}