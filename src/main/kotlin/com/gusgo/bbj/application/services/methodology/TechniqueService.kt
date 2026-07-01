package com.gusgo.bbj.application.services.methodology

import com.gusgo.bbj.application.dtos.methodology.TechniqueCreateRequest
import com.gusgo.bbj.application.dtos.methodology.TechniquePatchRequest
import com.gusgo.bbj.application.dtos.methodology.TechniqueResponse
import com.gusgo.bbj.application.dtos.methodology.TechniqueUpdateRequest
import com.gusgo.bbj.application.repositories.methodology.TechniqueRepository
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.domains.methodology.Technique
import com.gusgo.bbj.security.SecurityContextService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*

@Service
class TechniqueService(
    private val securityContextService: SecurityContextService,
    private val techniqueRepository: TechniqueRepository,
    private val academyRepository: AcademyRepository
) {
    fun create(request: TechniqueCreateRequest): TechniqueResponse? {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)

        val technique = Technique(
            name = request.name,
            academy = academyRef,
            description = request.description,
            videoUrl = request.videoUrl,
        )

        return techniqueRepository.save(technique).toResponse()
    }

    fun findById(id: UUID): TechniqueResponse? {
        val academyId = securityContextService.getCurrentAcademyId()
        val technique = techniqueRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Technique not found.")
        return technique.toResponse()
    }
    fun findAllByAcademy(): List<TechniqueResponse>? {
        val academyId = securityContextService.getCurrentAcademyId()
        val techniques = techniqueRepository.findAllByAcademyId(academyId)
        return techniques.map { it.toResponse() }
    }
    fun update(id: UUID, request: TechniqueUpdateRequest): TechniqueResponse? {
        val academyId = securityContextService.getCurrentAcademyId()
        val technique = techniqueRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Technique not found.")

        technique.name = request.name
        technique.description = request.description
        technique.active = request.active

        return techniqueRepository.save(technique).toResponse()
    }
    fun changeStatus(id: UUID, request: TechniquePatchRequest): TechniqueResponse? {
        val academyId = securityContextService.getCurrentAcademyId()
        val technique = techniqueRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Technique not found.")

        technique.active = request.active

        return techniqueRepository.save(technique).toResponse()
    }
    fun delete(id: UUID) {
        val academyId = securityContextService.getCurrentAcademyId()
        val technique = techniqueRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Technique not found.")

        techniqueRepository.delete(technique)
    }

    private fun Technique.toResponse() = TechniqueResponse(
        id = this.id!!,
        name = this.name,
        description = this.description!!,
        videoUrl = this.videoUrl,
        active = this.active
    )
}