package com.gusgo.bbj.domains.evolution

import com.gusgo.bbj.domains.pedagogy.Technique
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "evaluation_items",
    uniqueConstraints = [UniqueConstraint(columnNames = ["evaluation_id", "technique_id"])]
)
class EvaluationItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    val evaluation: Evaluation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    val technique: Technique,

    @Column(nullable = false)
    var score: Int = 0,

    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
)