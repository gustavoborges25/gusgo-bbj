package com.gusgo.bbj.rest.controllers.methodology

import com.gusgo.bbj.application.dtos.methodology.CurriculumRequest
import com.gusgo.bbj.application.dtos.methodology.CurriculumPatchRequest
import com.gusgo.bbj.application.dtos.methodology.CurriculumResponse
import com.gusgo.bbj.application.services.methodology.CurriculumService
import com.gusgo.bbj.rest.resources.RestResponse
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
@RequestMapping("/api/v1/curriculums")
@PreAuthorize("hasAnyRole('OWNER', 'INSTRUCTOR')")
class CurriculumController (
    private val curriculumService: CurriculumService
) {
    @PostMapping
    fun create(@RequestBody @Valid request: CurriculumRequest): ResponseEntity<RestResponse<CurriculumResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(curriculumService.create(request)))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RestResponse<CurriculumResponse>> =
        ResponseEntity
            .ok(RestResponse(curriculumService.findById(id)))

    @GetMapping
    fun getAllByAcademy(): ResponseEntity<RestResponse<List<CurriculumResponse>>> =
        ResponseEntity
            .ok(RestResponse(curriculumService.findAllByAcademy()))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: CurriculumRequest
    ): ResponseEntity<RestResponse<CurriculumResponse>> =
        ResponseEntity
            .ok(RestResponse(curriculumService.update(id, request)))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: UUID,
        @RequestBody @Valid request: CurriculumPatchRequest
    ): ResponseEntity<RestResponse<CurriculumResponse>> =
        ResponseEntity
            .ok(RestResponse(curriculumService.changeStatus(id, request)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        curriculumService.delete(id)
        return ResponseEntity.noContent().build()
    }
}