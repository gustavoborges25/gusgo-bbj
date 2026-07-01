package com.gusgo.bbj.application.services.methodology

import com.gusgo.bbj.application.dtos.methodology.CurriculumBeltResponse
import com.gusgo.bbj.application.dtos.methodology.CurriculumModuleResponse
import com.gusgo.bbj.application.dtos.methodology.CurriculumRequest
import com.gusgo.bbj.application.dtos.methodology.CurriculumPatchRequest
import com.gusgo.bbj.application.dtos.methodology.CurriculumResponse
import com.gusgo.bbj.application.dtos.methodology.CurriculumTechniqueResponse
import com.gusgo.bbj.application.repositories.methodology.BeltRepository
import com.gusgo.bbj.application.repositories.methodology.CurriculumModuleBeltRepository
import com.gusgo.bbj.application.repositories.methodology.CurriculumModuleBeltTechniqueRepository
import com.gusgo.bbj.application.repositories.methodology.CurriculumModuleRepository
import com.gusgo.bbj.application.repositories.methodology.CurriculumRepository
import com.gusgo.bbj.application.repositories.methodology.ModuleRepository
import com.gusgo.bbj.application.repositories.methodology.TechniqueRepository
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.domains.methodology.Curriculum
import com.gusgo.bbj.domains.methodology.CurriculumModule
import com.gusgo.bbj.domains.methodology.CurriculumModuleBelt
import com.gusgo.bbj.domains.methodology.CurriculumModuleBeltTechnique
import com.gusgo.bbj.domains.methodology.Module
import com.gusgo.bbj.security.SecurityContextService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class CurriculumService(
    private val academyRepository: AcademyRepository,
    private val securityContextService: SecurityContextService,
    private val curriculumRepository: CurriculumRepository,
    private val moduleRepository: ModuleRepository,
    private val curriculumModuleRepository: CurriculumModuleRepository,
    private val beltRepository: BeltRepository,
    private val curriculumModuleBeltRepository: CurriculumModuleBeltRepository,
    private val techniqueRepository: TechniqueRepository,
    private val curriculumModuleBeltTechniqueRepository: CurriculumModuleBeltTechniqueRepository
) {

    @Transactional
    fun create(request: CurriculumRequest): CurriculumResponse? {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)

        val curriculum = Curriculum(
            name = request.name,
            description = request.description,
            academy = academyRef
        )
        val savedCurriculum = curriculumRepository.save(curriculum)

        createCurriculumRelations(request, savedCurriculum)

        return savedCurriculum.toResponse()
    }
    fun findById(id: UUID): CurriculumResponse? {
        val academyId = securityContextService.getCurrentAcademyId()
        val curriculum = curriculumRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Curriculum not found.")
        return curriculum.toResponse()
    }
    fun findAllByAcademy(): List<CurriculumResponse>? {
        val academyId = securityContextService.getCurrentAcademyId()
        val curriculums = curriculumRepository.findAllByAcademyId(academyId)
        return curriculums.map { it.toResponse() }
    }

    @Transactional
    fun update(id: UUID, request: CurriculumRequest): CurriculumResponse? {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val existingCurriculum = curriculumRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Curriculum not found.")

        existingCurriculum.name = request.name
        existingCurriculum.description = request.description
        val updatedCurriculum = curriculumRepository.save(existingCurriculum)

        curriculumModuleRepository.deleteByCurriculumId(updatedCurriculum.id)
        curriculumModuleRepository.flush()

        createCurriculumRelations(request, updatedCurriculum)

        return updatedCurriculum.toResponse()
    }

    fun changeStatus(id: UUID, request: CurriculumPatchRequest): CurriculumResponse? {
        val academyId = securityContextService.getCurrentAcademyId()
        val curriculum = curriculumRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Curriculum not found.")

        curriculum.active = request.active

        return curriculumRepository.save(curriculum).toResponse()
    }
    fun delete(id: UUID) {
        val academyId = securityContextService.getCurrentAcademyId()
        val curriculum = curriculumRepository.findByIdAndAcademyId(id, academyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Curriculum not found.")

        curriculumRepository.delete(curriculum)
    }

    private fun createCurriculumRelations(request: CurriculumRequest, updatedCurriculum: Curriculum) {
        request.modules.forEach { moduleDto ->
            val moduleRef = moduleRepository.getReferenceById(moduleDto.id)

            val curriculumModule = CurriculumModule(
                curriculum = updatedCurriculum,
                module = moduleRef
            )
            val savedCurriculumModule = curriculumModuleRepository.save(curriculumModule)

            moduleDto.belts.forEach { beltDto ->
                val beltRef = beltRepository.getReferenceById(beltDto.id)

                val curriculumModuleBelt = CurriculumModuleBelt(
                    curriculumModule = savedCurriculumModule,
                    belt = beltRef
                )
                val savedCurriculumModuleBelt = curriculumModuleBeltRepository.save(curriculumModuleBelt)

                beltDto.techniques.forEach { techniqueDto ->
                    val techniqueRef = techniqueRepository.getReferenceById(techniqueDto.id)

                    val curriculumModuleBeltTechnique = CurriculumModuleBeltTechnique(
                        curriculumModuleBelt = savedCurriculumModuleBelt,
                        technique = techniqueRef,
                        required = techniqueDto.required,
                        minimumScore = techniqueDto.minimumScore
                    )
                    curriculumModuleBeltTechniqueRepository.save(curriculumModuleBeltTechnique)
                }
            }
        }
    }

    private fun Curriculum.toResponse() = CurriculumResponse(
        id = this.id!!,
        name = this.name,
        description = this.description,
        active = this.active,
        modules = this.modules.map { it.toResponse() }
    )

    private fun CurriculumModule.toResponse() = CurriculumModuleResponse(
        id = this.module.id!!,
        name = this.module.name,
        belts = this.belts.map { it.toResponse() }
    )

    private fun CurriculumModuleBelt.toResponse() = CurriculumBeltResponse(
        id = this.belt.id!!,
        name = this.belt.name,
        color = this.belt.color,
        techniques = this.techniques.map { it.toResponse() }
    )

    private fun CurriculumModuleBeltTechnique.toResponse() = CurriculumTechniqueResponse(
        id = this.technique.id!!,
        name = this.technique.name,
        required = this.required,
        minimumScore = this.minimumScore
    )

}