package com.gusgo.bbj.rest.controllers.registration

import com.gusgo.bbj.application.dtos.registration.LoginRequest
import com.gusgo.bbj.application.dtos.registration.UserCreateRequest
import com.gusgo.bbj.application.dtos.registration.UserResponse
import com.gusgo.bbj.application.dtos.registration.UserUpdateRequest
import com.gusgo.bbj.application.services.registration.UserService
import com.gusgo.bbj.rest.resources.RestResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    fun register(@RequestBody @Valid request: UserCreateRequest): ResponseEntity<RestResponse<UserResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(userService.registerUser(request)))

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<RestResponse<UserResponse>> =
        ResponseEntity
            .ok(RestResponse(userService.authenticate(request)))

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<RestResponse<UserResponse>> =
        ResponseEntity
            .ok(RestResponse(userService.findById(id)))

    @GetMapping()
    @PreAuthorize("hasRole('OWNER')")
    fun getAllUsersByAcademy(): ResponseEntity<RestResponse<List<UserResponse>>> =
        ResponseEntity
            .ok(RestResponse(userService.findAllByAcademy()))

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody @Valid request: UserUpdateRequest
    ): ResponseEntity<RestResponse<UserResponse>> =
        ResponseEntity
            .ok(RestResponse(userService.updateUser(id, request)))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}