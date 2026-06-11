package com.gusgo.bbj.domains.evolution

import com.gusgo.bbj.domains.core.Instructor
import com.gusgo.bbj.domains.member.Student
import com.gusgo.bbj.domains.pedagogy.Technique
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

enum class TechniqueProgressStatus {
    NOT_STARTED, LEARNING, EXECUTES, EXECUTES_WELL, MASTERED
}

@Entity
@Table(
    name = "student_technique_progress",
    uniqueConstraints = [UniqueConstraint(columnNames = ["student_id", "technique_id"])]
)
class StudentTechniqueProgress(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    val technique: Technique,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TechniqueProgressStatus = TechniqueProgressStatus.NOT_STARTED,

    @Column(nullable = false)
    var score: Int = 0,

    @Column(columnDefinition = "TEXT")
    var observations: String? = null,

    @Column(name = "last_evaluated_at")
    var lastEvaluatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by") // Nullable, pois o aluno pode marcar o início sozinho
    var evaluatedBy: Instructor? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
)