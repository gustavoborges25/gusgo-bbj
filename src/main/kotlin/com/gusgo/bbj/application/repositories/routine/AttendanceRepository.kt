package com.gusgo.bbj.application.repositories.routine

import com.gusgo.bbj.domains.routine.Attendance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AttendanceRepository : JpaRepository<Attendance, UUID> {
    fun findAllByClassRefId(classId: UUID): List<Attendance>

    fun countByStudentIdAndPresentTrue(studentId: UUID): Long

    fun countByStudentIdAndClassRefAcademyIdAndPresentTrue(studentId: UUID, academyId: UUID): Long

    fun existsByClassRefIdAndStudentId(classId: UUID, studentId: UUID): Boolean
}