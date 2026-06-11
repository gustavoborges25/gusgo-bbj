package com.gusgo.bbj.application.repositories.member

import com.gusgo.bbj.domains.member.StudentNote
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentNoteRepository : JpaRepository<StudentNote, UUID> {
    fun findAllByStudentIdOrderByCreatedAtDesc(studentId: UUID): List<StudentNote>

    fun findAllByStudentIdAndInstructorIdOrderByCreatedAtDesc(studentId: UUID, instructorId: UUID): List<StudentNote>
}