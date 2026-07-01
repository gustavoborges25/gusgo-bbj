package com.gusgo.bbj.application.repositories.registration

import com.gusgo.bbj.domains.registration.Academy
import com.gusgo.bbj.domains.registration.Student
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface StudentRepository: JpaRepository<Student, UUID> {
    fun findByIdAndAcademyId(id: UUID, academyId: UUID): Student?

    fun findAllByAcademyId(academyId: UUID): List<Student>

}