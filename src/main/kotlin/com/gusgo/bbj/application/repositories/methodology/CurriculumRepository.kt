package com.gusgo.bbj.application.repositories.methodology

import com.gusgo.bbj.domains.methodology.Curriculum
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CurriculumRepository : JpaRepository<Curriculum, UUID> {

    @Query("""
        SELECT DISTINCT c FROM Curriculum c 
        LEFT JOIN FETCH c.modules cm 
        LEFT JOIN FETCH cm.module 
        LEFT JOIN FETCH cm.belts cmb 
        LEFT JOIN FETCH cmb.belt 
        LEFT JOIN FETCH cmb.techniques cmbt 
        LEFT JOIN FETCH cmbt.technique 
        WHERE c.id = :id AND c.academy.id = :academyId
    """)
    fun findByIdAndAcademyId(id: UUID, academyId: UUID): Curriculum?

    @Query("""
        SELECT DISTINCT c FROM Curriculum c 
        LEFT JOIN FETCH c.modules cm 
        LEFT JOIN FETCH cm.module 
        LEFT JOIN FETCH cm.belts cmb 
        LEFT JOIN FETCH cmb.belt 
        LEFT JOIN FETCH cmb.techniques cmbt 
        LEFT JOIN FETCH cmbt.technique 
        WHERE c.academy.id = :academyId
        ORDER BY c.name ASC
    """)
    fun findAllByAcademyId(academyId: UUID): List<Curriculum>
}