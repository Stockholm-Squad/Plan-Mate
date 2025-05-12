package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditRepository
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildAudit
import java.util.*


class GetAuditUseCaseTest {

    private lateinit var auditRepository: AuditRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var getAuditUseCase: GetAuditUseCase


    @BeforeEach
    fun setUp() {
        auditRepository = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        getAuditUseCase = GetAuditUseCase(auditRepository, getProjectsUseCase, manageTasksUseCase)
    }

    @Test
    fun `getAuditsForProjectByName() should return audits related to the project`() = runTest {
        // Given
        val projectName = "project"
        val projectId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit = buildAudit(entityTypeId = projectId)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit)
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk {
            every { id } returns projectId
        }

        // When
        val result = getAuditUseCase.getAuditsForProjectByName(projectName)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(projectId)
    }

    @Test
    fun `getAuditsForProjectByName() should return an empty list if no audits are found`() = runTest {
        // Given
        val projectName = "project"
        coEvery { auditRepository.getAllAudits() } returns emptyList()
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk {
            every { id } returns UUID.randomUUID()
        }

        // When
        val result = getAuditUseCase.getAuditsForProjectByName(projectName)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForTaskByName() should return audits related to the task`() = runTest {
        // Given

        val taskName = "task"
        val taskId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit = buildAudit(entityTypeId = taskId)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit)
        coEvery { manageTasksUseCase.getAllTasks() } returns listOf(mockk {
            every { id } returns taskId
            every { name } returns taskName
        })

        // When
        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(taskId)
    }

    @Test
    fun `getAuditsForTaskByName() should return an empty list if no audits are found`() = runTest {
        // Given

        val taskName = "task"

        coEvery { auditRepository.getAllAudits() } returns emptyList()
        coEvery { manageTasksUseCase.getAllTasks() } returns emptyList()

        // When
        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForUserById() should return audits related to the user`() = runTest {
        // Given
        val userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit = buildAudit(entityTypeId = userId)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit)

        // When
        val result = getAuditUseCase.getAuditsForUserById(userId)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().userId).isEqualTo(userId)
    }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits are found`() = runTest {
        // Given
        val userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")

        coEvery { auditRepository.getAllAudits() } returns emptyList()

        // When
        val result = getAuditUseCase.getAuditsForUserById(userId)

        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository`() = runTest {
        // Given
        val userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit = buildAudit(entityTypeId = userId)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit)

        // When
        getAuditUseCase.getAuditsForUserById(userId)

        // Then
        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository list`() = runTest {
        // Given
        val userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit1 = buildAudit(userId = userId, entityType = EntityType.TASK)
        val audit2 = buildAudit(userId = userId, entityType = EntityType.PROJECT)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit1, audit2)

        // When
        getAuditUseCase.getAuditsForUserById(userId)

        // Then
        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should return audits related to the correct user when multiple audits exist`() =
        runTest {
            // Given
            val userId1 = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
            val userId2 = UUID.fromString("a5a85f64-6737-4562-b9fc-3c963f66abc1")
            val audit1 = buildAudit(userId = userId1)
            val audit2 = buildAudit(userId = userId2)

            coEvery { auditRepository.getAllAudits() } returns listOf(audit1, audit2)

            // When
            val result = getAuditUseCase.getAuditsForUserById(userId1)

            // Then
            assertThat(result).hasSize(1)
            assertThat(result.first().userId).isEqualTo(userId1)
        }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits match the userId`() = runTest {
        // Given
        val userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")
        val audit1 = buildAudit(entityTypeId = userId)
        val audit2 = buildAudit(entityTypeId = userId)

        coEvery { auditRepository.getAllAudits() } returns listOf(audit1, audit2)

        // When
        val result = getAuditUseCase.getAuditsForUserById(UUID.randomUUID())

        // Then
        assertThat(result).isEmpty()
    }
}
