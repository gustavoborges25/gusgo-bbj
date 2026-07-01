package com.gusgo.bbj.application.services.registration

import com.gusgo.bbj.application.dtos.registration.StudentImportCommitRequest
import com.gusgo.bbj.application.dtos.registration.StudentImportLineResponse
import com.gusgo.bbj.application.dtos.registration.StudentImportValidationResponse
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
class StudentImportService(
    private val beltRepository: BeltRepository,
    private val academyRepository: AcademyRepository,
    private val studentRepository: StudentRepository,
    private val securityContextService: SecurityContextService
) {
    fun validateFile(file: MultipartFile): StudentImportValidationResponse? {
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "The uploaded file is empty.")
        }
        val studentLines = mutableListOf<StudentImportLineResponse>()

        val beltsMap = beltRepository.findAll().associateBy { it.name.lowercase().trim() }

        getWorkbook(file).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            for (row in sheet) {
                if (row.rowNum == 0) continue

                val name = row.getCellText(0)?.trim() ?: continue
                if (name.isBlank()) continue

                val errors = mutableListOf<String>()

                val birthDate = row.getCellDate(1)
                if (birthDate == null) {
                    errors.add("Data de nascimento inválida ou obrigatória (Use o formato DD/MM/AAAA).")
                }

                val inputBeltName = row.getCellText(2)?.trim()?.lowercase()
                var beltId: UUID? = null
                var beltName = ""
                var beltColor = ""

                if (inputBeltName.isNullOrBlank()) {
                    errors.add("O nome da faixa é obrigatório.")
                } else {
                    val belt = beltsMap[inputBeltName]

                    if (belt != null) {
                        beltId = belt.id
                        beltName = belt.name
                        beltColor = belt.color
                    } else {
                        errors.add("A faixa '$inputBeltName' não foi encontrada no sistema.")
                    }
                }

                // Grau (Coluna 3)
                val degree = row.getCellNumeric(3)?.toInt() ?: 0
                if (degree < 0) {
                    errors.add("O grau do aluno não pode ser negativo.")
                }

                studentLines.add(
                    StudentImportLineResponse(
                        rowNumber = row.rowNum + 1,
                        name = name,
                        birthDate = birthDate,
                        beltId = beltId,
                        beltName = beltName,
                        beltColor = beltColor,
                        degree = degree,
                        isValid = errors.isEmpty(),
                        errors = errors
                    )
                )
            }
        }

        val validCount = studentLines.count { it.isValid }

        return StudentImportValidationResponse(
            totalRows = studentLines.size,
            validRowsCount = validCount,
            hasErrors = validCount < studentLines.size,
            students = studentLines
        )
    }

    private fun getWorkbook(file: MultipartFile): Workbook {
        val filename = file.originalFilename ?: throw ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "Invalid file name."
        )
        val extension = filename.substringAfterLast(".", "").lowercase()

        return when (extension) {
            "xls" -> HSSFWorkbook(file.inputStream)
            "xlsx" -> XSSFWorkbook(file.inputStream)
            else -> throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid file format. Please upload an Excel file (.xls or .xlsx)."
            )
        }
    }

    private fun Row.getCellText(cellIndex: Int): String? {
        val cell = this.getCell(cellIndex) ?: return null
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> String.format("%.0f", cell.numericCellValue)
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> null
        }
    }

    private fun Row.getCellNumeric(cellIndex: Int): Double? {
        val cell = this.getCell(cellIndex) ?: return null
        return when (cell.cellType) {
            CellType.NUMERIC -> cell.numericCellValue
            CellType.STRING -> cell.stringCellValue.toDoubleOrNull()
            else -> null
        }
    }

    private fun Row.getCellDate(cellIndex: Int): LocalDate? {
        val cell = this.getCell(cellIndex) ?: return null
        return try {
            when (cell.cellType) {
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cell.localDateTimeCellValue.toLocalDate()
                    } else {
                        null
                    }
                }
                CellType.STRING -> {
                    LocalDate.parse(cell.stringCellValue.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    @Transactional
    fun commitImport(request: StudentImportCommitRequest) {
        if (request.students.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Students not found.")
        }

        val loggedAcademyId = securityContextService.getCurrentAcademyId()
        val academyRef = academyRepository.getReferenceById(loggedAcademyId)


        val studentsToSave = request.students.map { line ->
            val beltRef = beltRepository.getReferenceById(line.beltId)


            Student(
                name = line.name,
                birthDate = line.birthDate,
                belt = beltRef,
                degree = line.degree,
                academy = academyRef,
                active = true
            )
        }
        studentRepository.saveAll(studentsToSave)
    }
}