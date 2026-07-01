package com.gusgo.bbj.application.services.registration

import com.gusgo.bbj.application.dtos.registration.LoginRequest
import com.gusgo.bbj.application.dtos.registration.UserCreateRequest
import com.gusgo.bbj.application.dtos.registration.UserResponse
import com.gusgo.bbj.application.dtos.registration.UserUpdateRequest
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.application.repositories.registration.UserRepository
import com.gusgo.bbj.domains.registration.User
import com.gusgo.bbj.security.SecurityContextService
import com.gusgo.bbj.security.TokenService
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

private const val USER_NOT_FOUND_ = "User not found."

private const val INVALID_EMAIL_OR_PASSWORD_ = "Invalid email or password."

@Service
class UserService(
    private val userRepository: UserRepository,
    private val academyRepository: AcademyRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,
    private val securityContextService: SecurityContextService
) {
    @Transactional
    fun registerUser(request: UserCreateRequest): UserResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)

        validatesExistingEmail(request.email)

        val passwordHash = passwordEncoder.encode(request.password)

        val user = User(
            academy = academyRef,
            name = request.name,
            email = request.email,
            passwordHash = passwordHash,
            role = request.role
        )

        return userRepository.save(user).toResponse()
    }



    @Transactional(readOnly = true)
    fun authenticate(request: LoginRequest): UserResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_EMAIL_OR_PASSWORD_)

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_EMAIL_OR_PASSWORD_)
        }

        val jwtToken = tokenService.generateToken(user)

        return UserResponse(
            id = user.id!!,
            name = user.name,
            email = user.email,
            role = user.role,
            token = jwtToken
        )
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): UserResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val user = userRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_)
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByAcademy(): List<UserResponse> {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val users = userRepository.findAllByAcademyId(loggedAcademyId)
        return users.map { it.toResponse() }
    }

    @Transactional
    fun updateUser(id: UUID, request: UserUpdateRequest): UserResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val user = userRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_)

        if (user.email != request.email) {
            validatesExistingEmail(request.email)
        }

        user.name = request.name
        user.email = request.email
        user.role = request.role

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun deleteUser(id: UUID) {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val user = userRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_)

        userRepository.delete(user)
    }

    private fun User.toResponse() = UserResponse(
        id = this.id!!,
        name = this.name,
        email = this.email,
        role = this.role
    )

    private fun validatesExistingEmail(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use by another user.")
        }
    }
}