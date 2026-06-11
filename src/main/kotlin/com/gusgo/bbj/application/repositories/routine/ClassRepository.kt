package com.gusgo.bbj.application.repositories.routine

import com.gusgo.bbj.domains.routine.Class
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
interface ClassRepository : JpaRepository<Class, UUID> {
    fun findAllByAcademyIdOrderByClassDateDescCreatedAtDesc(academyId: UUID): List<Class>

    fun findAllByAcademyIdAndClassDate(academyId: UUID, classDate: LocalDate): List<Class>

    fun findAllByInstructorIdOrderByClassDateDesc(instructorId: UUID): List<Class>
}