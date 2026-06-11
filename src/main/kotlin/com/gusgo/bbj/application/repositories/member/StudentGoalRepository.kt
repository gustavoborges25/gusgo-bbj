package com.gusgo.bbj.application.repositories.member

import com.gusgo.bbj.domains.member.GoalStatus
import com.gusgo.bbj.domains.member.StudentGoal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StudentGoalRepository : JpaRepository<StudentGoal, UUID> {
    fun findAllByStudentIdOrderByCreatedAtDesc(studentId: UUID): List<StudentGoal>

    fun findAllByStudentIdAndStatusOrderByTargetDateAsc(studentId: UUID, status: GoalStatus): List<StudentGoal>
}