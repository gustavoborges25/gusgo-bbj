package com.gusgo.bbj.domains.methodology

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
    name = "curriculum_module_belt_techniques",
    uniqueConstraints = [UniqueConstraint(columnNames = ["curriculum_module_belt_id", "technique_id"])]
)
class CurriculumModuleBeltTechnique(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_module_belt_id", nullable = false)
    val curriculumModuleBelt: CurriculumModuleBelt,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    val technique: Technique,

    @Column(nullable = false)
    var required: Boolean = false,

    @Column(name = "minimum_score", nullable = false)
    var minimumScore: Int = 0,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null
)