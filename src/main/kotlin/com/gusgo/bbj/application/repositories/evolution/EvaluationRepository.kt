package com.gusgo.bbj.application.repositories.evolution

import com.gusgo.bbj.domains.evolution.Evaluation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EvaluationRepository : JpaRepository<Evaluation, UUID> {
    fun findAllByStudentIdOrderByEvaluationDateDesc(studentId: UUID): List<Evaluation>

    fun findAllByInstructorIdOrderByEvaluationDateDesc(instructorId: UUID): List<Evaluation>
}