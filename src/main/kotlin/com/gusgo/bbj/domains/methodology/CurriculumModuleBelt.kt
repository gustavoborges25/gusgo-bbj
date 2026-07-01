package com.gusgo.bbj.domains.methodology

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "curriculum_module_belts",
    uniqueConstraints = [UniqueConstraint(columnNames = ["curriculum_module_id", "belt_id"])]
)
class CurriculumModuleBelt(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_module_id", nullable = false)
    val curriculumModule: CurriculumModule,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "belt_id", nullable = false)
    val belt: Belt,

    @OneToMany(mappedBy = "curriculumModuleBelt", cascade = [CascadeType.ALL], orphanRemoval = true)
    val techniques: MutableSet<CurriculumModuleBeltTechnique> = mutableSetOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null
)