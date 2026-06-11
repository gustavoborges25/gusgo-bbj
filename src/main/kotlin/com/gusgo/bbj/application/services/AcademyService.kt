package com.gusgo.bbj.application.services

import com.gusgo.bbj.application.dtos.AcademyDto
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.domains.core.Academy
import com.gusgo.bbj.rest.exceptions.BusinessException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AcademyService(
    private val academyRepository: AcademyRepository
) {

    fun create(academyDto: AcademyDto): AcademyDto {
        val academySaved: Academy = academyRepository.save(Academy(
            name = academyDto.name,
            email = "teste",
            phone = TODO(),
            createdAt = TODO(),
            updatedAt = TODO()
        ))
        return AcademyDto(id = academySaved.id.toString(), name = academySaved.name)
    }

    fun getAll() : List<AcademyDto> {
        val academyList = academyRepository.findAll()
        val academyDtoList = mutableListOf<AcademyDto>()
        for (academy in academyList) {
            academyDtoList.add(AcademyDto(
                id = academy.id.toString(),
                name = academy.name,
            ))
        }
        return academyDtoList
    }

    fun getById(id: UUID): AcademyDto {
        val academy: Academy = academyRepository.findByIdOrNull(id)
            ?: throw BusinessException("Academy with ID $id not found")
        return AcademyDto(
            id = academy.id.toString(),
            name = academy.name,
        )
    }

    fun update(id: UUID, academyDto: AcademyDto): AcademyDto {
        val academy: Academy = academyRepository.findByIdOrNull(id)
            ?: throw BusinessException("Academy with ID $id not found")
        academy.name = academyDto.name
        val academySaved: Academy = academyRepository.save(academy)
        return AcademyDto(
            id = academySaved.id.toString(),
            name = academySaved.name,
        )
    }

    fun delete(id: UUID) {
        val academy: Academy = academyRepository.findByIdOrNull(id)
            ?: throw BusinessException("Academy with ID $id not found")
        academyRepository.delete(academy)
    }

}