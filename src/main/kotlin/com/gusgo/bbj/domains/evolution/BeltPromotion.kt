package com.gusgo.bbj.domains.evolution

import com.gusgo.bbj.domains.member.Student
import com.gusgo.bbj.domains.pedagogy.Belt
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "belt_promotions")
class BeltPromotion(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,

    // Controle de Faixas (Sempre obrigatórias)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_belt_id", nullable = false)
    val fromBelt: Belt,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_belt_id", nullable = false)
    var toBelt: Belt,

    // Controle de Graus (Permite manter a mesma faixa e progredir no grau)
    @Column(name = "from_degree", nullable = false)
    val fromDegree: Int = 0,

    @Column(name = "to_degree", nullable = false)
    var toDegree: Int = 0,

    @Column(name = "promotion_date", nullable = false)
    var promotionDate: LocalDate = LocalDate.now(),

    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
)