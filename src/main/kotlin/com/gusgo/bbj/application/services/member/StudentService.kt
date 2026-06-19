package com.gusgo.bbj.application.services.member

import com.gusgo.bbj.application.dtos.member.StudentCreateRequest
import com.gusgo.bbj.application.dtos.member.StudentPatchRequest
import com.gusgo.bbj.application.dtos.member.StudentResponse
import com.gusgo.bbj.application.dtos.member.StudentUpdateRequest
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.application.repositories.core.UserRepository
import com.gusgo.bbj.application.repositories.member.StudentRepository
import com.gusgo.bbj.application.repositories.pedagogy.BeltRepository
import com.gusgo.bbj.domains.member.Student
import com.gusgo.bbj.security.SecurityContextService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val academyRepository: AcademyRepository,
    private val beltRepository: BeltRepository,
    private val securityContextService: SecurityContextService
) {

    @Transactional
    fun create(request: StudentCreateRequest): StudentResponse {
        val academyId = securityContextService.getCurrentAcademyId()
        val academy = academyRepository.findByIdOrNull(academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified academy does not exist.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")


        val student = Student(
            academy = academy,
            name = request.name,
            birthDate = request.birthDate,
            belt = belt,
            degree = request.degree,
            notes = request.notes
        )

        return studentRepository.save(student).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): StudentResponse {
        val student = studentRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")
        return student.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByAcademy(): List<StudentResponse> {
        val academyId = securityContextService.getCurrentAcademyId()
        val students = studentRepository.findAllByAcademyId(academyId)
        return students.map { it.toResponse() }
    }

    @Transactional
    fun update(id: UUID, request: StudentUpdateRequest): StudentResponse {
        val student = studentRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        if (student.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify students from another academy.")
        }

        student.name = request.name
        student.birthDate = request.birthDate
        student.belt = belt
        student.degree = request.degree
        student.active = request.active
        student.notes = request.notes

        return studentRepository.save(student).toResponse()
    }

    @Transactional
    fun deleteStudent(id: UUID) {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()

        val student = studentRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        if (student.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete students from another academy.")
        }

        studentRepository.delete(student)
    }

    @Transactional
    fun changeStatus(id: UUID, request: StudentPatchRequest): StudentResponse? {
        val student = studentRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        if (student.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify students from another academy.")
        }
        student.active = request.active

        return studentRepository.save(student).toResponse()
    }


    private fun Student.toResponse() = StudentResponse(
        id = this.id!!,
        name = this.name,
        birthDate = this.birthDate,
        beltId = this.belt.id!!,
        beltName = this.belt.name,
        beltColor = this.belt.color,
        degree = this.degree,
        joinDate = this.joinDate,
        active = this.active,
        notes = this.notes
    )
}