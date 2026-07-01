package com.gusgo.bbj.rest.controllers.registration

import com.gusgo.bbj.application.dtos.registration.AcademyRequest
import com.gusgo.bbj.application.dtos.registration.AcademyResponse
import com.gusgo.bbj.application.services.registration.AcademyService
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
@RequestMapping("/api/v1/academies")
@PreAuthorize("hasRole('OWNER')")
class AcademyController(
    private val academyService: AcademyService
)
{
    @PostMapping
    fun create(@RequestBody @Valid academyDto: AcademyRequest): ResponseEntity<RestResponse<AcademyResponse>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(academyService.create(academyDto)))

    @GetMapping
    fun listAll(): ResponseEntity<RestResponse<List<AcademyResponse>>> =
        ResponseEntity.ok(RestResponse(academyService.listAll()))

    @GetMapping("/{id}")
    fun findById(@PathVariable id: UUID): ResponseEntity<RestResponse<AcademyResponse>> =
        ResponseEntity.ok(RestResponse(academyService.findById(id)))

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody academyDto: AcademyRequest): ResponseEntity<RestResponse<AcademyResponse>> =
        ResponseEntity.ok(RestResponse(academyService.update(id, academyDto)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        academyService.delete(UUID.fromString(id))
        return ResponseEntity.noContent().build()
    }
}