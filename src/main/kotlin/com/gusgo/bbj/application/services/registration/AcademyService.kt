package com.gusgo.bbj.application.services.registration

import com.gusgo.bbj.application.dtos.registration.AcademyRequest
import com.gusgo.bbj.application.dtos.registration.AcademyResponse
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.domains.registration.Academy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AcademyService(
    private val academyRepository: AcademyRepository
) {

    @Transactional
    fun create(request: AcademyRequest): AcademyResponse {
        validatesExistingEmail(request.email)
        val academy = Academy(
            name = request.name,
            email = request.email,
            phone = request.phone
        )
        return academyRepository.save(academy).toResponse()
    }

    @Transactional(readOnly = true)
    fun listAll(): List<AcademyResponse> =
        academyRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun findById(id: UUID): AcademyResponse =
        getAcademyOrThrow(id).toResponse()


    @Transactional
    fun update(id: UUID, request: AcademyRequest): AcademyResponse {
        val academy = getAcademyOrThrow(id)
        if (academy.email != request.email) {
            validatesExistingEmail(request.email)
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

    private fun getAcademyOrThrow(id: UUID): Academy =
        academyRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Academy not found.")

    private fun Academy.toResponse() = AcademyResponse(
        id = this.id!!,
        name = this.name,
        email = this.email,
        phone = this.phone
    )
    private fun validatesExistingEmail(email: String) {
        if (academyRepository.existsByEmail(email)) {
            throw IllegalArgumentException("This email is already in use by another gym.")
        }
    }
}