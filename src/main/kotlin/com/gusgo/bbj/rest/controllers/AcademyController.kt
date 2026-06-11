package com.gusgo.bbj.rest.controllers

import com.gusgo.bbj.application.dtos.AcademyDto
import com.gusgo.bbj.application.services.AcademyService
import com.gusgo.bbj.rest.resources.RestResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping("/academies")
class AcademyController(
    private val academyService: AcademyService
)
{
    @PostMapping
    fun create(@RequestBody academyDto: AcademyDto): ResponseEntity<RestResponse<AcademyDto>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(RestResponse(academyService.create(academyDto)))

    @GetMapping
    fun getAll(): ResponseEntity<RestResponse<List<AcademyDto>>> =
        ResponseEntity.ok(RestResponse(academyService.getAll()))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<RestResponse<AcademyDto>> =
        ResponseEntity.ok(RestResponse(academyService.getById(UUID.fromString(id))))

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody academyDto: AcademyDto): ResponseEntity<RestResponse<AcademyDto>> =
        ResponseEntity.ok(RestResponse(academyService.update(UUID.fromString(id), academyDto)))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        academyService.delete(UUID.fromString(id))
        return ResponseEntity.noContent().build()
    }

}