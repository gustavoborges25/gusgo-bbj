package com.gusgo.bbj.application.services.core

import com.gusgo.bbj.application.dtos.core.LoginRequest
import com.gusgo.bbj.application.dtos.core.UserCreateRequest
import com.gusgo.bbj.application.dtos.core.UserResponse
import com.gusgo.bbj.application.dtos.core.UserUpdateRequest
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.application.repositories.core.UserRepository
import com.gusgo.bbj.domains.core.User
import com.gusgo.bbj.security.TokenService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val academyRepository: AcademyRepository,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun registerUser(request: UserCreateRequest): UserResponse {
        val academy = academyRepository.findByIdOrNull(request.academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified academy does not exist.")

        if (userRepository.existsByEmail(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use by another user.")
        }

        val passwordHash = passwordEncoder.encode(request.password)

        val user = User(
            academy = academy,
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
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password.")
        }

        val jwtToken = tokenService.generateToken(user)

        return UserResponse(
            id = user.id!!,
            academyId = user.academy.id!!,
            name = user.name,
            email = user.email,
            role = user.role,
            token = jwtToken // 👈 Envia o token na resposta
        )
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByAcademy(academyId: UUID): List<UserResponse> {
        if (!academyRepository.existsById(academyId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified academy does not exist.")
        }
        val users = userRepository.findAllByAcademyId(academyId)
        return users.map { it.toResponse() }
    }

    @Transactional
    fun updateUser(id: UUID, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")

        if (user.email != request.email && userRepository.existsByEmail(request.email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use by another user.")
        }

        user.name = request.name
        user.email = request.email

        return userRepository.save(user).toResponse()
    }

    @Transactional
    fun deleteUser(id: UUID) {
        val user = userRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")

        userRepository.delete(user)
    }

    private fun User.toResponse() = UserResponse(
        id = this.id!!,
        academyId = this.academy.id!!,
        name = this.name,
        email = this.email,
        role = this.role
    )
}