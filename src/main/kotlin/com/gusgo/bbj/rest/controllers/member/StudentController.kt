package com.gusgo.bbj.rest.controllers.member

import com.gusgo.bbj.application.dtos.member.StudentCreateRequest
import com.gusgo.bbj.application.dtos.member.StudentPatchRequest
import com.gusgo.bbj.application.dtos.member.StudentResponse
import com.gusgo.bbj.application.dtos.member.StudentUpdateRequest
import com.gusgo.bbj.application.services.member.StudentService
import com.gusgo.bbj.rest.resources.RestResponse
import com.gusgo.bbj.security.SecurityContextService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/students")
@PreAuthorize("hasAnyRole('OWNER', 'PROFESSOR')")
class StudentController(
    private val studentService: StudentService,
    private val securityContextService: SecurityContextService
) {

    @PostMapping
    fun create(@RequestBody @Valid request: StudentCreateRequest): ResponseEntity<RestResponse<StudentResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(studentService.create(request)))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RestResponse<StudentResponse>> =
        ResponseEntity
            .ok(RestResponse(studentService.findById(id)))

    @GetMapping
    fun getAllByAcademy(): ResponseEntity<RestResponse<List<StudentResponse>>> {
        val authenticatedAcademyId = securityContextService.getCurrentAcademyId()
        return ResponseEntity
            .ok(RestResponse(studentService.findAllByAcademy(authenticatedAcademyId)))
    }


    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: StudentUpdateRequest
    ): ResponseEntity<RestResponse<StudentResponse>> =
        ResponseEntity
            .ok(RestResponse(studentService.update(id, request)))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: UUID,
        @RequestBody @Valid request: StudentPatchRequest
    ): ResponseEntity<RestResponse<StudentResponse>> =
        ResponseEntity
            .ok(RestResponse(studentService.changeStatus(id, request)))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        studentService.deleteStudent(id)
        return ResponseEntity.noContent().build()
    }
}