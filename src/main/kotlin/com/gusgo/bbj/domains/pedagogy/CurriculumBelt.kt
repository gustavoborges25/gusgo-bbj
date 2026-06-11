package com.gusgo.bbj.domains.pedagogy

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "curriculum_belts",
    uniqueConstraints = [UniqueConstraint(columnNames = ["curriculum_id", "belt_id"])]
)
class CurriculumBelt(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    val curriculum: Curriculum,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belt_id", nullable = false)
    val belt: Belt,

    @Column(name = "order_position", nullable = false)
    var orderPosition: Int,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null
)