package com.gusgo.bbj.domains.evolution

import com.gusgo.bbj.domains.core.Instructor
import com.gusgo.bbj.domains.member.Student
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "evaluations")
class Evaluation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    val instructor: Instructor,

    @Column(name = "evaluation_date", nullable = false)
    var evaluationDate: LocalDate = LocalDate.now(),

    @Column(name = "general_notes", columnDefinition = "TEXT")
    var generalNotes: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
)