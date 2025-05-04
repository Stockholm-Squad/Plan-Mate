package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ManageAuditSystemUseCaseTest {

    private lateinit var manageAuditSystemUseCase: ManageAuditSystemUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var manageTasksUseCase: ManageTasksUseCase

    @BeforeEach
    fun setUp() {
        auditSystemRepository = mockk(relaxed = true)
        manageProjectUseCase = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        manageAuditSystemUseCase = ManageAuditSystemUseCase(
            auditSystemRepository,
            manageProjectUseCase,
            manageTasksUseCase
        )
    }

    @Test
    fun `addAuditsEntries should return true when successful`() {
        val data = listOf(
            AuditSystem(
                entityType = EntityType.TASK,
                entityTypeId = UUID.randomUUID(),
                description = "Task updated",
                userId = UUID.randomUUID(),
                dateTime = LocalDateTime(2025, 12, 19, 12, 0)
            )
        )
        every { auditSystemRepository.addAuditsEntries(data) } returns Result.success(true)

        val result = manageAuditSystemUseCase.addAuditsEntries(data)

        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `addAuditsEntries should return failure when repository fails`() {
        val data = listOf(
            AuditSystem(
                entityType = EntityType.TASK,
                entityTypeId = UUID.randomUUID(),
                description = "Task update failed",
                userId = UUID.randomUUID(),
                dateTime = LocalDateTime(2025, 12, 19, 12, 0)
            )
        )
        every { auditSystemRepository.addAuditsEntries(data) } returns Result.failure(Exception("DB error"))

        val result = manageAuditSystemUseCase.addAuditsEntries(data)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAuditsByUserId should return filtered audit list`() {
        val userId = UUID.randomUUID()
        val auditList = listOf(
            AuditSystem(
                entityType = EntityType.PROJECT,
                entityTypeId = UUID.randomUUID(),
                description = "Project created",
                userId = userId,
                dateTime = LocalDateTime(2023, 1, 1, 10, 0)
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(auditList)

        val result = manageAuditSystemUseCase.getAuditsByUserId(userId)

        assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getAuditsByUserId should return failure on repository error`() {
        val userId = UUID.randomUUID()
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        val result = manageAuditSystemUseCase.getAuditsByUserId(userId)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getProjectAuditsByName should return filtered results if project found`() {
        val projectId = UUID.randomUUID()
        val projectName = "My Project"
        val audits = listOf(
            AuditSystem(
                entityType = EntityType.PROJECT,
                entityTypeId = projectId,
                description = "Created",
                userId = UUID.randomUUID(),
                dateTime = LocalDateTime(2023, 1, 1, 10, 0)
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(audits)
        every { manageProjectUseCase.getProjectByName(projectName) } returns Result.success(
            mockk { every { id } returns projectId }
        )

        val result = manageAuditSystemUseCase.getProjectAuditsByName(projectName)

        assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getProjectAuditsByName should return failure if project not found`() {
        val projectName = "Missing Project"
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(emptyList())
        every { manageProjectUseCase.getProjectByName(projectName) } returns Result.failure(Exception("Not found"))

        val result = manageAuditSystemUseCase.getProjectAuditsByName(projectName)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getTaskAuditsByName should return filtered results if task found`() {
        val taskId = UUID.randomUUID()
        val taskName = "My Task"
        val audits = listOf(
            AuditSystem(
                entityType = EntityType.TASK,
                entityTypeId = taskId,
                description = "Changed",
                userId = UUID.randomUUID(),
                dateTime = LocalDateTime(2023, 1, 1, 10, 0)
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(audits)
        every { manageTasksUseCase.getTaskIdByName(taskName) } returns Result.success(taskId)

        val result = manageAuditSystemUseCase.getTaskAuditsByName(taskName)

        assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getTaskAuditsByName should return failure if task not found`() {
        val taskName = "Invalid Task"
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(emptyList())
        every { manageTasksUseCase.getTaskIdByName(taskName) } returns Result.failure(Exception("Not found"))

        val result = manageAuditSystemUseCase.getTaskAuditsByName(taskName)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getTaskAuditsByName should return failure if auditRepository fails`() {
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("DB down"))

        val result = manageAuditSystemUseCase.getTaskAuditsByName("Task")

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getProjectAuditsByName should return failure if auditRepository fails`() {
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("DB down"))

        val result = manageAuditSystemUseCase.getProjectAuditsByName("Project")

        assertThat(result.isFailure).isTrue()
    }
}
