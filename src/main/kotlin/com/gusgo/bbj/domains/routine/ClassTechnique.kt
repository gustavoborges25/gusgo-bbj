package com.gusgo.bbj.domains.routine

import com.gusgo.bbj.domains.pedagogy.Technique
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "class_techniques",
    uniqueConstraints = [UniqueConstraint(columnNames = ["class_id", "technique_id"])]
)
class ClassTechnique(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    val classRef: Class,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technique_id", nullable = false)
    val technique: Technique,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null
)