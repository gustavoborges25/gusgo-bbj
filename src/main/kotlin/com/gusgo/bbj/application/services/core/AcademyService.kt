package com.gusgo.bbj.application.services.core

import com.gusgo.bbj.application.dtos.core.AcademyRequest
import com.gusgo.bbj.application.dtos.core.AcademyResponse
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.domains.core.Academy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AcademyService(
    private val academyRepository: AcademyRepository
) {

    @Transactional(readOnly = true)
    fun listAll(): List<AcademyResponse> =
        academyRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: UUID): AcademyResponse =
        getAcademyOrThrow(id).toResponse()

    @Transactional
    fun create(request: AcademyRequest): AcademyResponse {
        if (academyRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("E-mail já cadastrado para outra academia.")
        }
        val academy = Academy(name = request.name, email = request.email, phone = request.phone)
        return academyRepository.save(academy).toResponse()
    }

    @Transactional
    fun update(id: UUID, request: AcademyRequest): AcademyResponse {
        val academy = getAcademyOrThrow(id)
        if (academy.email != request.email && academyRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("E-mail já em uso por outra academia.")
        }
        academy.name = request.name
        academy.email = request.email
        academy.phone = request.phone
        return academyRepository.save(academy).toResponse()
    }

    @Transactional
    fun delete(id: UUID) {
        val academy = getAcademyOrThrow(id)
        academyRepository.delete(academy)
    }

    fun getAcademyOrThrow(id: UUID): Academy =
        academyRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Academia não encontrada.")

    private fun Academy.toResponse() = AcademyResponse(
        id = this.id!!, name = this.name, email = this.email, phone = this.phone
    )
}