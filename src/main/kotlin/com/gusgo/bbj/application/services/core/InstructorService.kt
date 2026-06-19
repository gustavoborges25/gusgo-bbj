package com.gusgo.bbj.application.services.core

import com.gusgo.bbj.application.dtos.core.InstructorCreateRequest
import com.gusgo.bbj.application.dtos.core.InstructorPatchRequest
import com.gusgo.bbj.application.dtos.core.InstructorResponse
import com.gusgo.bbj.application.dtos.core.InstructorUpdateRequest
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.application.repositories.core.InstructorRepository
import com.gusgo.bbj.application.repositories.core.UserRepository
import com.gusgo.bbj.application.repositories.pedagogy.BeltRepository
import com.gusgo.bbj.domains.core.Instructor
import com.gusgo.bbj.domains.core.User
import com.gusgo.bbj.domains.core.UserRole
import com.gusgo.bbj.security.SecurityContextService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class InstructorService(
    private val instructorRepository: InstructorRepository,
    private val academyRepository: AcademyRepository,
    private val userRepository: UserRepository,
    private val beltRepository: BeltRepository,
    private val securityContextService: SecurityContextService,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun create(request: InstructorCreateRequest): InstructorResponse {
        val academyId = securityContextService.getCurrentAcademyId()
        val academy = academyRepository.findByIdOrNull(academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified academy does not exist.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        userRepository.findByEmail(request.email)?.run {
            throw ResponseStatusException(HttpStatus.CONFLICT, "The email is already used")
        }

        val user = User(
            academy = academy,
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.PROFESSOR
        )
        val savedUser = userRepository.save(user)

        val instructor = Instructor(
            academy = academy,
            user = savedUser,
            belt = belt,
            degree = request.degree,
            active = true
        )

        return instructorRepository.save(instructor).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): InstructorResponse {
        val instructor = instructorRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")
        return instructor.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByAcademy(): List<InstructorResponse> {
        val academyId = securityContextService.getCurrentAcademyId()
        val instructors = instructorRepository.findAllByAcademyId(academyId)
        return instructors.map { it.toResponse() }
    }

    @Transactional
    fun update(id: UUID, request: InstructorUpdateRequest): InstructorResponse {
        val instructor = instructorRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        if (instructor.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify instructors from another academy.")
        }

        val user = instructor.user
        user.name = request.name
        if (!request.password.isNullOrBlank()) {
            user.passwordHash = passwordEncoder.encode(request.password)
        }
        instructor.belt = belt
        instructor.degree = request.degree
        instructor.active = request.active

        return instructorRepository.save(instructor).toResponse()
    }

    @Transactional
    fun deleteInstructor(id: UUID) {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()

        val instructor = instructorRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")

        if (instructor.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete instructors from another academy.")
        }

        instructorRepository.delete(instructor)
    }

    @Transactional
    fun changeStatus(id: UUID, request: InstructorPatchRequest): InstructorResponse? {
        val instructor = instructorRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")

        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        if (instructor.academy.id != loggedAcademyId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify instructors from another academy.")
        }
        instructor.active = request.active

        return instructorRepository.save(instructor).toResponse()
    }

    private fun Instructor.toResponse() = InstructorResponse(
        id = this.id!!,
        name = this.user.name,
        email = this.user.email,
        beltId = this.belt.id!!,
        beltName = this.belt.name,
        beltColor = this.belt.color,
        degree = this.degree,
        active = this.active
    )
}