package com.gusgo.bbj.rest.controllers.registration

import com.gusgo.bbj.application.dtos.registration.StudentCreateRequest
import com.gusgo.bbj.application.dtos.registration.StudentImportCommitRequest
import com.gusgo.bbj.application.dtos.registration.StudentImportValidationResponse
import com.gusgo.bbj.application.dtos.registration.StudentPatchRequest
import com.gusgo.bbj.application.dtos.registration.StudentResponse
import com.gusgo.bbj.application.dtos.registration.StudentUpdateRequest
import com.gusgo.bbj.application.services.registration.StudentImportService
import com.gusgo.bbj.application.services.registration.StudentService
import com.gusgo.bbj.rest.resources.RestResponse
import jakarta.validation.Valid
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/students")
@PreAuthorize("hasAnyRole('OWNER', 'INSTRUCTOR')")
class StudentController(
    private val studentService: StudentService,
    private val studentImportService: StudentImportService
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

    @GetMapping("/import/template")
    fun downloadTemplate(): ResponseEntity<Resource> {
        val resource = ClassPathResource("templates/modelo_importacao_alunos.xlsx")
        if (!resource.exists()) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"modelo_importacao_alunos.xlsx\"")
            .body(resource)
    }

    @PostMapping("/import/validate")
    fun validateImportFile( @RequestParam("file") file: MultipartFile): ResponseEntity<RestResponse<StudentImportValidationResponse>> =
        ResponseEntity.ok(RestResponse(studentImportService.validateFile(file)))

    @PostMapping("/import/commit")
    fun commitImport(@RequestBody request: StudentImportCommitRequest): ResponseEntity<RestResponse<Void>> {
        studentImportService.commitImport(request);
        return ResponseEntity.ok().build();
    }
}