package com.gusgo.bbj.rest.controllers.methodology

import com.gusgo.bbj.application.dtos.methodology.TechniqueCreateRequest
import com.gusgo.bbj.application.dtos.methodology.TechniquePatchRequest
import com.gusgo.bbj.application.dtos.methodology.TechniqueResponse
import com.gusgo.bbj.application.dtos.methodology.TechniqueUpdateRequest
import com.gusgo.bbj.application.services.methodology.TechniqueService
import com.gusgo.bbj.rest.resources.RestResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping("/api/v1/techniques")
class TechniqueController (
    private val techniqueService: TechniqueService
) {
    @PostMapping
    fun create(@RequestBody @Valid request: TechniqueCreateRequest): ResponseEntity<RestResponse<TechniqueResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(techniqueService.create(request)))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ResponseEntity<RestResponse<TechniqueResponse>> =
        ResponseEntity
            .ok(RestResponse(techniqueService.findById(id)))

    @GetMapping
    fun getAllByAcademy(): ResponseEntity<RestResponse<List<TechniqueResponse>>> =
        ResponseEntity
            .ok(RestResponse(techniqueService.findAllByAcademy()))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody @Valid request: TechniqueUpdateRequest
    ): ResponseEntity<RestResponse<TechniqueResponse>> =
        ResponseEntity
            .ok(RestResponse(techniqueService.update(id, request)))

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: UUID,
        @RequestBody @Valid request: TechniquePatchRequest
    ): ResponseEntity<RestResponse<TechniqueResponse>> =
        ResponseEntity
            .ok(RestResponse(techniqueService.changeStatus(id, request)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        techniqueService.delete(id)
        return ResponseEntity.noContent().build()
    }
}