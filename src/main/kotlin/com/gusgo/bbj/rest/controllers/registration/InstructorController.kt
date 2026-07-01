package com.gusgo.bbj.rest.controllers.registration

import com.gusgo.bbj.application.dtos.registration.InstructorCreateRequest
import com.gusgo.bbj.application.dtos.registration.InstructorPatchRequest
import com.gusgo.bbj.application.dtos.registration.InstructorResponse
import com.gusgo.bbj.application.dtos.registration.InstructorUpdateRequest
import com.gusgo.bbj.application.services.registration.InstructorService
import com.gusgo.bbj.rest.resources.RestResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/instructors")
@PreAuthorize("hasRole('OWNER')")
class InstructorController(
    private val instructorService: InstructorService
) {

    @PostMapping
    fun create(@RequestBody @Valid request: InstructorCreateRequest): ResponseEntity<RestResponse<InstructorResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(instructorService.create(request)))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RestResponse<InstructorResponse>> =
        ResponseEntity
            .ok(RestResponse(instructorService.findById(id)))

    @GetMapping
    fun getAllByAcademy(): ResponseEntity<RestResponse<List<InstructorResponse>>> =
        ResponseEntity
            .ok(RestResponse(instructorService.findAllByAcademy()))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: InstructorUpdateRequest
    ): ResponseEntity<RestResponse<InstructorResponse>> =
        ResponseEntity
            .ok(RestResponse(instructorService.update(id, request)))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: UUID,
        @RequestBody @Valid request: InstructorPatchRequest
    ): ResponseEntity<RestResponse<InstructorResponse>> =
        ResponseEntity
            .ok(RestResponse(instructorService.changeStatus(id, request)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        instructorService.deleteInstructor(id)
        return ResponseEntity.noContent().build()
    }
}