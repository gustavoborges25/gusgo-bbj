package com.gusgo.bbj.application.services

import com.gusgo.bbj.application.dtos.core.InstructorCreateRequest
import com.gusgo.bbj.application.dtos.core.InstructorPatchRequest
import com.gusgo.bbj.application.dtos.core.InstructorUpdateRequest
import com.gusgo.bbj.application.repositories.core.AcademyRepository
import com.gusgo.bbj.application.repositories.core.InstructorRepository
import com.gusgo.bbj.application.repositories.core.UserRepository
import com.gusgo.bbj.application.repositories.pedagogy.BeltRepository
import com.gusgo.bbj.application.services.core.InstructorService
import com.gusgo.bbj.domains.core.Academy
import com.gusgo.bbj.domains.core.Instructor
import com.gusgo.bbj.domains.core.User
import com.gusgo.bbj.domains.core.UserRole
import com.gusgo.bbj.domains.pedagogy.Belt
import com.gusgo.bbj.security.SecurityContextService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

class InstructorServiceTest {
    private val instructorRepository: InstructorRepository = mockk()
    private val academyRepository: AcademyRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val beltRepository: BeltRepository = mockk()
    private val securityContextService: SecurityContextService = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val instructorService = InstructorService(
        instructorRepository,
        academyRepository,
        userRepository,
        beltRepository,
        securityContextService,
        passwordEncoder
    )

    // Mocks auxiliares reutilizáveis
    private val academyId = UUID.randomUUID()
    private val beltId = UUID.randomUUID()
    private val instructorId = UUID.randomUUID()

    private val mockAcademy = mockk<Academy> {
        every { id } returns academyId
    }

    private val mockBelt = mockk<Belt> {
        every { id } returns beltId
        every { name } returns "Black Belt"
        every { color } returns "Black"
    }

    @Test
    fun `should create instructor successfully`() {
        // Arrange
        val request = InstructorCreateRequest(
            name = "John Doe",
            email = "john@doe.com",
            password = "securePassword",
            beltId = beltId,
            degree = 1
        )

        val mockUser = mockk<User> {
            every { name } returns request.name
            every { email } returns request.email
        }

        val mockInstructor = mockk<Instructor> {
            every { id } returns instructorId
            every { user } returns mockUser
            every { belt } returns mockBelt
            every { degree } returns request.degree
            every { active } returns true
        }

        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { academyRepository.findByIdOrNull(academyId) } returns mockAcademy
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { userRepository.findByEmail(request.email) } returns null
        every { passwordEncoder.encode(request.password) } returns "encodedHash"
        every { userRepository.save(any<User>()) } returns mockUser
        every { instructorRepository.save(any<Instructor>()) } returns mockInstructor

        // Act
        val response = instructorService.create(request)

        // Assert
        assertNotNull(response)
        assertEquals(instructorId, response.id)
        assertEquals(request.name, response.name)
        assertEquals(request.email, response.email)
        assertTrue(response.active)

        verify(exactly = 1) { userRepository.save(any<User>()) }
        verify(exactly = 1) { instructorRepository.save(any<Instructor>()) }
    }

    @Test
    fun `should throw NOT_FOUND when academy does not exist during creation`() {
        // Arrange
        val request = InstructorCreateRequest(
            name = "John Doe",
            email = "john@doe.com",
            password = "password",
            beltId = beltId,
            degree = 1
        )

        every { securityContextService.getCurrentAcademyId() } returns academyId
        // Simula que a academia não foi encontrada
        every { academyRepository.findByIdOrNull(academyId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.create(request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("The specified academy does not exist.", exception.reason)

        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun `should throw NOT_FOUND when belt does not exist during creation`() {
        // Arrange
        val request = InstructorCreateRequest(
            name = "John Doe",
            email = "john@doe.com",
            password = "password",
            beltId = beltId,
            degree = 1
        )

        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { academyRepository.findByIdOrNull(academyId) } returns mockAcademy

        // Simula que a faixa (Belt) informada não foi encontrada no banco
        every { beltRepository.findByIdOrNull(beltId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.create(request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("The specified belt does not exist.", exception.reason)

        // Garante que o fluxo travou e o usuário/instrutor NÃO foram salvos
        verify(exactly = 0) { userRepository.findByEmail(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { instructorRepository.save(any()) }
    }

    @Test
    fun `should throw CONFLICT when email is already used during creation`() {
        // Arrange
        val request = InstructorCreateRequest(
            name = "John Doe",
            email = "duplicate@email.com",
            password = "password",
            beltId = beltId,
            degree = 0
        )

        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { academyRepository.findByIdOrNull(academyId) } returns mockAcademy
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { userRepository.findByEmail(request.email) } returns mockk<User>() // E-mail já existe

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.create(request)
        }

        assertEquals(HttpStatus.CONFLICT, exception.statusCode)
        assertEquals("The email is already used", exception.reason)

        verify(exactly = 0) { userRepository.save(any()) }
        verify(exactly = 0) { instructorRepository.save(any()) }
    }

    @Test
    fun `should return instructor response when found by id`() {
        // Arrange
        val mockUser = mockk<User> {
            every { name } returns "Jane Doe"
            every { email } returns "jane@doe.com"
        }
        val mockInstructor = mockk<Instructor> {
            every { id } returns instructorId
            every { user } returns mockUser
            every { belt } returns mockBelt
            every { degree } returns 2
            every { active } returns true
        }

        every { instructorRepository.findByIdOrNull(instructorId) } returns mockInstructor

        // Act
        val response = instructorService.findById(instructorId)

        // Assert
        assertNotNull(response)
        assertEquals(instructorId, response.id)
        assertEquals("Jane Doe", response.name)
    }

    @Test
    fun `should throw NOT_FOUND when instructor id does not exist`() {
        // Arrange
        every { instructorRepository.findByIdOrNull(instructorId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.findById(instructorId)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Instructor not found.", exception.reason)
    }

    @Test
    fun `should return a list of instructors for the current academy`() {
        // Arrange
        val mockUser1 = mockk<User> {
            every { name } returns "Instructor One"
            every { email } returns "one@academy.com"
        }
        val mockInstructor1 = mockk<Instructor> {
            every { id } returns UUID.randomUUID()
            every { user } returns mockUser1
            every { belt } returns mockBelt
            every { degree } returns 1
            every { active } returns true
        }

        val mockUser2 = mockk<User> {
            every { name } returns "Instructor Two"
            every { email } returns "two@academy.com"
        }
        val mockInstructor2 = mockk<Instructor> {
            every { id } returns UUID.randomUUID()
            every { user } returns mockUser2
            every { belt } returns mockBelt
            every { degree } returns 3
            every { active } returns false
        }

        // Cria a lista simulada que o banco retornaria
        val mockInstructorsList = listOf(mockInstructor1, mockInstructor2)


        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { instructorRepository.findAllByAcademyId(academyId) } returns mockInstructorsList.toMutableList()

        // Act
        val response = instructorService.findAllByAcademy()

        // Assert
        assertNotNull(response)
        assertEquals(2, response.size) // Garante que retornou os 2 itens

        // Valida o mapeamento do primeiro instrutor
        assertEquals("Instructor One", response[0].name)
        assertEquals("one@academy.com", response[0].email)
        assertTrue(response[0].active)

        // Valida o mapeamento do segundo instrutor
        assertEquals("Instructor Two", response[1].name)
        assertEquals("two@academy.com", response[1].email)
        assertEquals(3, response[1].degree)

        verify(exactly = 1) { instructorRepository.findAllByAcademyId(academyId) }
    }

    @Test
    fun `should throw FORBIDDEN when updating instructor from another academy`() {
        // Arrange
        val anotherAcademyId = UUID.randomUUID()
        val mockAnotherAcademy = mockk<Academy> {
            every { id } returns anotherAcademyId
        }

        val mockInstructor = mockk<Instructor> {
            every { academy } returns mockAnotherAcademy // Academia diferente da logada
        }

        val request = InstructorUpdateRequest(
            name = "Updated Name",
            password = "newPassword",
            beltId = beltId,
            degree = 3,
            active = true
        )

        every { instructorRepository.findByIdOrNull(instructorId) } returns mockInstructor
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { securityContextService.getCurrentAcademyId() } returns academyId // Academia logada diferente

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.update(instructorId, request)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        assertEquals("You do not have permission to modify instructors from another academy.", exception.reason)
    }

    @Test
    fun `should throw NOT_FOUND when updating a non-existing instructor`() {
        // Arrange
        val request = InstructorUpdateRequest(
            name = "Updated Name",
            password = "",
            beltId = beltId,
            degree = 1,
            active = true
        )

        // Simula que o instrutor não foi encontrado
        every { instructorRepository.findByIdOrNull(instructorId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.update(instructorId, request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Instructor not found.", exception.reason)

        verify(exactly = 0) { instructorRepository.save(any()) }
    }

    @Test
    fun `should throw NOT_FOUND when belt does not exist during updating`() {
        // Arrange
        val request = InstructorUpdateRequest(
            name = "John Doe",
            password = "password",
            beltId = beltId,
            degree = 1,
            active = true
        )
        val userMock = User(
            academy = mockAcademy,
            name = "Old Name",
            email = "alex@test.com",
            passwordHash = "oldHash",
            role = UserRole.PROFESSOR
        )
        val instructorMock = Instructor(
            academy = mockAcademy,
            user = userMock,
            belt = mockk(),
            degree = 0,
            active = true
        ).apply { id = instructorId }

        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { instructorRepository.findByIdOrNull(instructorId) } returns instructorMock

        // Simula que a faixa (Belt) informada não foi encontrada no banco
        every { beltRepository.findByIdOrNull(beltId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.update(instructorId, request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("The specified belt does not exist.", exception.reason)

        // Garante que o fluxo travou e o usuário/instrutor NÃO foram salvos
        verify(exactly = 0) { instructorRepository.save(any()) }
    }

    @Test
    fun `should update instructor successfully including password`() {
        // Arrange
        val request = InstructorUpdateRequest(
            name = "Alex Silva",
            password = "newSecretPassword",
            beltId = beltId,
            degree = 2,
            active = false
        )

        // Entidades originais antes da alteração
        val userMock = User(
            academy = mockAcademy,
            name = "Old Name",
            email = "alex@test.com",
            passwordHash = "oldHash",
            role = UserRole.PROFESSOR
        )

        val instructorMock = Instructor(
            academy = mockAcademy,
            user = userMock,
            belt = mockk(),
            degree = 0,
            active = true
        ).apply { id = instructorId }

        every { instructorRepository.findByIdOrNull(instructorId) } returns instructorMock
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { passwordEncoder.encode(request.password) } returns "newEncodedHash"
        every { instructorRepository.save(any<Instructor>()) } returns instructorMock

        // Act
        val response = instructorService.update(instructorId, request)

        // Assert
        assertNotNull(response)
        assertEquals("Alex Silva", response.name)
        assertEquals(2, response.degree)
        assertEquals(false, response.active)
        assertEquals("newEncodedHash", userMock.passwordHash) // Garante que a senha mudou

        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { instructorRepository.save(any<Instructor>()) }
    }

    @Test
    fun `should update instructor successfully without changing password when it is blank`() {
        // Arrange
        val request = InstructorUpdateRequest(
            name = "Alex Silva",
            password = "", // Senha em branco (o front-end enviou vazio para manter a atual)
            beltId = beltId,
            degree = 2,
            active = true
        )

        val userMock = User(
            academy = mockAcademy,
            name = "Old Name",
            email = "alex@test.com",
            passwordHash = "keepThisHash", // Esta hash deve ser mantida
            role = UserRole.PROFESSOR
        )

        val instructorMock = Instructor(
            academy = mockAcademy,
            user = userMock,
            belt = mockk(),
            degree = 0,
            active = true
        ).apply { id = instructorId }

        every { instructorRepository.findByIdOrNull(instructorId) } returns instructorMock
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { instructorRepository.save(any<Instructor>()) } returns instructorMock

        // Act
        val response = instructorService.update(instructorId, request)

        // Assert
        assertNotNull(response)
        assertEquals("keepThisHash", userMock.passwordHash) // Garante que a senha antiga NÃO mudou

        // Verifica que o encoder NUNCA foi chamado para uma senha vazia
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 1) { instructorRepository.save(any<Instructor>()) }
    }

    @Test
    fun `should update instructor successfully when password is explicitly null`() {
        // Arrange
        val request = InstructorUpdateRequest(
            name = "Alex Silva",
            password = null, // Testando o terceiro braço da condicional (Null)
            beltId = beltId,
            degree = 2,
            active = true
        )

        val userMock = User(
            academy = mockAcademy,
            name = "Old Name",
            email = "alex@test.com",
            passwordHash = "keepThisHash",
            role = UserRole.PROFESSOR
        )

        val instructorMock = Instructor(
            academy = mockAcademy,
            user = userMock,
            belt = mockk(),
            degree = 0,
            active = true
        ).apply { id = instructorId }

        every { instructorRepository.findByIdOrNull(instructorId) } returns instructorMock
        every { beltRepository.findByIdOrNull(beltId) } returns mockBelt
        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { instructorRepository.save(any<Instructor>()) } returns instructorMock

        // Act
        instructorService.update(instructorId, request)

        // Assert
        assertEquals("keepThisHash", userMock.passwordHash) // Garante que não quebrou e manteve a hash
        verify(exactly = 0) { passwordEncoder.encode(any()) }
    }

    @Test
    fun `should delete instructor successfully`() {
        // Arrange
        val mockInstructor = mockk<Instructor>()
        every { instructorRepository.findByIdOrNull(instructorId) } returns mockInstructor
        every { instructorRepository.delete(mockInstructor) } returns Unit

        // Act
        instructorService.deleteInstructor(instructorId)

        // Assert
        verify(exactly = 1) { instructorRepository.delete(mockInstructor) }
    }

    @Test
    fun `should throw NOT_FOUND when deleting a non-existing instructor`() {
        // Arrange
        // Simula que o instrutor não foi encontrado
        every { instructorRepository.findByIdOrNull(instructorId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.deleteInstructor(instructorId)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Instructor not found.", exception.reason)

        verify(exactly = 0) { instructorRepository.delete(any()) }
    }

    @Test
    fun `should change instructor status successfully`() {
        // Arrange
        val request = InstructorPatchRequest(active = false)

        val userMock = mockk<User> {
            every { name } returns "Instructor Test"
            every { email } returns "test@academy.com"
        }

        // Instanciamos o objeto real para garantir que o estado interno mude
        val instructorMock = Instructor(
            academy = mockAcademy,
            user = userMock,
            belt = mockBelt,
            degree = 3,
            active = true // Começa como true
        ).apply { id = instructorId }

        every { instructorRepository.findByIdOrNull(instructorId) } returns instructorMock
        every { securityContextService.getCurrentAcademyId() } returns academyId
        every { instructorRepository.save(any<Instructor>()) } returns instructorMock

        // Act
        val response = instructorService.changeStatus(instructorId, request)

        // Assert
        assertNotNull(response)
        assertEquals(false, response?.active) // Garante que mudou para false conforme a request
        assertEquals(false, instructorMock.active) // Garante que a entidade foi modificada

        verify(exactly = 1) { instructorRepository.save(instructorMock) }
    }

    @Test
    fun `should throw FORBIDDEN when changing status of an instructor from another academy`() {
        // Arrange
        val anotherAcademyId = UUID.randomUUID()
        val mockAnotherAcademy = mockk<Academy> {
            every { id } returns anotherAcademyId
        }

        val mockInstructor = mockk<Instructor> {
            every { academy } returns mockAnotherAcademy // Academia dona do instrutor
        }

        val request = InstructorPatchRequest(active = false)

        every { instructorRepository.findByIdOrNull(instructorId) } returns mockInstructor
        every { securityContextService.getCurrentAcademyId() } returns academyId // Academia logada atual

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.changeStatus(instructorId, request)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        assertEquals("You do not have permission to modify instructors from another academy.", exception.reason)

        // Garante que o banco de dados NUNCA foi tocado
        verify(exactly = 0) { instructorRepository.save(any()) }
    }

    @Test
    fun `should throw NOT_FOUND when changing status of a non-existing instructor`() {
        // Arrange
        val request = InstructorPatchRequest(active = false)

        // Simula que o instrutor não foi encontrado no banco de dados
        every { instructorRepository.findByIdOrNull(instructorId) } returns null

        // Act & Assert
        val exception = assertThrows<ResponseStatusException> {
            instructorService.changeStatus(instructorId, request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("Instructor not found.", exception.reason)

        // Garante que o fluxo foi interrompido antes de verificar a segurança ou salvar
        verify(exactly = 0) { securityContextService.getCurrentAcademyId() }
        verify(exactly = 0) { instructorRepository.save(any()) }
    }
}