package com.gusgo.bbj.application.services.registration

import com.gusgo.bbj.application.dtos.registration.StudentCreateRequest
import com.gusgo.bbj.application.dtos.registration.StudentImportCommitRequest
import com.gusgo.bbj.application.dtos.registration.StudentImportLineResponse
import com.gusgo.bbj.application.dtos.registration.StudentImportValidationResponse
import com.gusgo.bbj.application.dtos.registration.StudentPatchRequest
import com.gusgo.bbj.application.dtos.registration.StudentResponse
import com.gusgo.bbj.application.dtos.registration.StudentUpdateRequest
import com.gusgo.bbj.application.repositories.methodology.BeltRepository
import com.gusgo.bbj.application.repositories.registration.AcademyRepository
import com.gusgo.bbj.application.repositories.registration.StudentRepository
import com.gusgo.bbj.domains.registration.Student
import com.gusgo.bbj.security.SecurityContextService
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val academyRepository: AcademyRepository,
    private val beltRepository: BeltRepository,
    private val securityContextService: SecurityContextService
) {

    @Transactional
    fun create(request: StudentCreateRequest): StudentResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        val student = Student(
            academy = academyRef,
            name = request.name,
            birthDate = request.birthDate,
            belt = belt,
            degree = request.degree,
            notes = request.notes
        )

        return studentRepository.save(student).toResponse()
    }

    @Transactional(readOnly = true)
    fun findById(id: UUID): StudentResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val student = studentRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")
        return student.toResponse()
    }

    @Transactional(readOnly = true)
    fun findAllByAcademy(): List<StudentResponse> {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val students = studentRepository.findAllByAcademyId(loggedAcademyId)
        return students.map { it.toResponse() }
    }

    @Transactional
    fun update(id: UUID, request: StudentUpdateRequest): StudentResponse {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val student = studentRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        val belt = beltRepository.findByIdOrNull(request.beltId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "The specified belt does not exist.")

        student.name = request.name
        student.birthDate = request.birthDate
        student.belt = belt
        student.degree = request.degree
        student.active = request.active
        student.notes = request.notes

        return studentRepository.save(student).toResponse()
    }

    @Transactional
    fun deleteStudent(id: UUID) {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val student = studentRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        studentRepository.delete(student)
    }

    @Transactional
    fun changeStatus(id: UUID, request: StudentPatchRequest): StudentResponse? {
        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val student = studentRepository.findByIdAndAcademyId(id, loggedAcademyId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.")

        student.active = request.active

        return studentRepository.save(student).toResponse()
    }


    private fun Student.toResponse() = StudentResponse(
        id = this.id!!,
        name = this.name,
        birthDate = this.birthDate,
        beltId = this.belt.id!!,
        beltName = this.belt.name,
        beltColor = this.belt.color,
        degree = this.degree,
        joinDate = this.joinDate,
        active = this.active,
        notes = this.notes
    )


}