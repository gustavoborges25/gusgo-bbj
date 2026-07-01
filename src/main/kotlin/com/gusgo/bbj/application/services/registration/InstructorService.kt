package com.gusgo.bbj.application.services.registration

import com.gusgo.bbj.application.dtos.registration.InstructorCreateRequest
import com.gusgo.bbj.application.dtos.registration.InstructorPatchRequest
import com.gusgo.bbj.application.dtos.registration.InstructorResponse
import com.gusgo.bbj.application.dtos.registration.InstructorUpdateRequest
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.application.repositories.registration.InstructorRepository
import com.gusgo.bbj.application.repositories.registration.UserRepository
import com.gusgo.bbj.application.repositories.methodology.BeltRepository
import com.gusgo.bbj.domains.registration.Instructor
import com.gusgo.bbj.domains.registration.User
import com.gusgo.bbj.domains.registration.UserRole
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
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        userRepository.findByEmail(request.email)?.run {
            throw ResponseStatusException(HttpStatus.CONFLICT, "The email is already used")
        }

        val user = User(
            academy = academyRef,
            name = request.name,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.INSTRUCTOR
        )
        val savedUser = userRepository.save(user)

        val instructor = Instructor(
            academy = academyRef,
            user = savedUser,
            belt = belt,
            degree = request.degree,
            active = true
        )

        return instructorRepository.save(instructor).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): InstructorResponse {
        val academyId = securityContextService.getCurrentAcademyId()
        val instructor = instructorRepository.findByIdAndAcademyId(id, academyId)
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
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val instructor = instructorRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

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

        val instructor = instructorRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")


        instructorRepository.delete(instructor)
    }

    @Transactional
    fun changeStatus(id: UUID, request: InstructorPatchRequest): InstructorResponse? {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val instructor = instructorRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found.")


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