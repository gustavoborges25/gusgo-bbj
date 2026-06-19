package com.gusgo.bbj.rest.controllers.member

import com.gusgo.bbj.application.dtos.member.StudentCreateRequest
import com.gusgo.bbj.application.dtos.member.StudentPatchRequest
import com.gusgo.bbj.application.dtos.member.StudentResponse
import com.gusgo.bbj.application.dtos.member.StudentUpdateRequest
import com.gusgo.bbj.application.services.member.StudentService
import com.gusgo.bbj.rest.resources.RestResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/students")
@PreAuthorize("hasAnyRole('OWNER', 'PROFESSOR')")
class StudentController(
    private val studentService: StudentService
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
        return ResponseEntity
            .ok(RestResponse(studentService.findAllByAcademy()))
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