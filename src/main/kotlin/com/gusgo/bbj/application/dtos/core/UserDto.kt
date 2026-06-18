package com.gusgo.bbj.application.dtos.core

import com.gusgo.bbj.domains.core.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class UserCreateRequest(
    @field:NotNull(message = "Academy ID is required.")
    val academyId: UUID,

    @field:NotBlank(message = "Name is required.")
    @field:Size(max = 255, message = "Name must not exceed 255 characters.")
    val name: String,

    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Invalid email format.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    @field:Size(min = 6, message = "Password must be at least 6 characters long.")
    val password: String,

    @field:NotNull(message = "User role is required.")
    val role: UserRole
)

data class UserUpdateRequest(
    @field:NotBlank(message = "Name is required.")
    @field:Size(max = 255, message = "Name must not exceed 255 characters.")
    val name: String,

    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Invalid email format.")
    val email: String
)

data class LoginRequest(
    @field:NotBlank(message = "Email is required.")
    @field:Email(message = "Invalid email format.")
    val email: String,

    @field:NotBlank(message = "Password is required.")
    val password: String
)

data class UserResponse(
    val id: java.util.UUID,
    val academyId: java.util.UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val token: String? = null // 👈 Novo campo opcional para transportar o JWT
)