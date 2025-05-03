package logic.usecase.audit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import logic.model.entities.*
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ManageAuditSystemUseCaseTest {

    private lateinit var manageAuditSystemUseCase: ManageAuditSystemUseCase
    private lateinit var projectUseCase: ManageProjectUseCase
    private lateinit var tasksUseCase: ManageTasksUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository
    private val task = Task(
        name = "test",
        description = "test",
        stateId = UUID.randomUUID(),
        createdDate = LocalDateTime.parse("2020-01-01"),
        updatedDate = LocalDateTime.parse("2020-01-01")
    )

    private val project = Project(
        name = "test",
        stateId = UUID.randomUUID(),
    )

    private val user = User(
        username = "test",
        hashedPassword = "test",
    )

    private val taskData = listOf(
        AuditSystem(
            entityType = EntityType.TASK,
            entityTypeId = task.id,
            description = "change from 'Open' to 'In Progress'",
            userId = user.id,
            dateTime = LocalDateTime.parse("2020-01-01")
        )
    )

    private val projectData = listOf(
        AuditSystem(
            entityType = EntityType.PROJECT,
            entityTypeId = project.id,
            description = "change from 'Open' to 'In Progress'",
            userId = user.id,
            dateTime = LocalDateTime.parse("2020-01-01")
        )
    )


    @BeforeEach
    fun setUp() {
        auditSystemRepository = mockk(relaxed = true)
        projectUseCase = mockk(relaxed = true)
        tasksUseCase = mockk(relaxed = true)
        manageAuditSystemUseCase = ManageAuditSystemUseCase(auditSystemRepository ,projectUseCase , tasksUseCase )
    }

    @Test
    fun `recordAuditsEntries should return true when successfully added task changes`() {
        //given

        every { auditSystemRepository.addAuditsEntries(taskData) } returns Result.success(true)

        //When
        val result = manageAuditSystemUseCase.addAuditsEntries(taskData)

        // then
        Truth.assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `recordAuditsEntries should return false when successfully added task changes`() {
        //given

        every { auditSystemRepository.addAuditsEntries(taskData) } returns Result.failure(Exception("error"))

        //When
        val result = manageAuditSystemUseCase.addAuditsEntries(taskData)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }



    @Test
    fun `getTaskChangeLogsById should return audit system for task when found`() {

        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(taskData)

        // when
        val result = manageAuditSystemUseCase.getTaskAuditsByName(task.name)

        // then
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `getTaskChangeLogsById should return failure audit system for task when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = manageAuditSystemUseCase.getTaskAuditsByName(task.name)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAuditSystemByID should return audit system for task when found`() {
        //given

        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(taskData)

        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId(user.id)

        // then
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getAuditSystemByID should return failure audit system for task when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId(user.id)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getProjectChanges should return audit system for project when found`() {
        //given

        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(projectData)
        // when
        val result = manageAuditSystemUseCase.getProjectAuditsByName(project.name)

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getProjectChanges should return failure audit system for project when not found`() {
        //given

        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))
        // when
        val result = manageAuditSystemUseCase.getProjectAuditsByName(project.name)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getChangesByUser should return audit system when found`() {
        //given

        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(projectData)
        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId(user.id)

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getChangesByUser should return failure audit system when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))
        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId(user.id)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }
}