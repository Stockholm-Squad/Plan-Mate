package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.utils.DateHandlerImp
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

    private val projectName = "Project1"
    private val taskName = "Task1"
    private val dateTime = DateHandlerImp().getCurrentDateTime()

    private val projectId = UUID.fromString("e3a85f64-5717-4562-b3fc-2c963f66dabc")
    private val projectAuditId = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1")
    private val userProjectId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1")

    private val taskId = UUID.fromString("c3a85f64-5717-4562-b3fc-2c963f66abcd")
    private val taskAuditId = UUID.fromString("5fa85f64-5717-4562-b3fc-2c963f66bfa2")
    private val taskAuditId1 = UUID.fromString("9fa85f64-5717-4562-b3fc-2c963f66bfa4")
    private val userTaskId = UUID.fromString("8fa85f64-5717-4562-b3fc-2c963f66bfa3")
    private val userTaskId1 = UUID.fromString("7fa85f64-5717-4562-b3fc-2c963f66bfa5")

    private val auditProject = buildAudit(
        id = projectAuditId,
        entityType = EntityType.PROJECT,
        entityTypeId = projectId,
        userId = userProjectId,
        description = "Added Project",
        createdAt = dateTime
    )

    private val auditTask = buildAudit(
        id = taskAuditId,
        entityType = EntityType.TASK,
        entityTypeId = taskId,
        userId = userTaskId,
        description = "Added Task",
        createdAt = dateTime
    )

    private val auditTask1 = buildAudit(
        id = taskAuditId1,
        entityType = EntityType.TASK,
        entityTypeId = taskId,
        userId = userTaskId1,
        description = "Added Task",
        createdAt = dateTime
    )

    @BeforeEach
    fun setUp() {
        auditRepository = mockk(relaxed = true)
        getProjectsUseCase = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        getAuditUseCase = GetAuditUseCase(auditRepository, getProjectsUseCase, manageTasksUseCase)
    }

    @Test
    fun `getAuditsForProjectByName() should return audits related to the project`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditProject)
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk {
            every { id } returns projectId
        }

        val result = getAuditUseCase.getAuditsForProjectByName(projectName)

        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(projectId)
    }

    @Test
    fun `getAuditsForProjectByName() should return an empty list if no audits are found`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns emptyList()
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk {
            every { id } returns UUID.randomUUID()
        }

        val result = getAuditUseCase.getAuditsForProjectByName(projectName)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForTaskByName() should return audits related to the task`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)
        coEvery { manageTasksUseCase.getAllTasks() } returns listOf(mockk {
            every { id } returns taskId
            every { name } returns taskName
        })

        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(taskId)
    }

    @Test
    fun `getAuditsForTaskByName() should return an empty list if no audits are found`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns emptyList()
        coEvery { manageTasksUseCase.getAllTasks() } returns emptyList()

        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForUserById() should return audits related to the user`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)

        val result = getAuditUseCase.getAuditsForUserById(userTaskId)

        assertThat(result).isNotEmpty()
        assertThat(result.first().userId).isEqualTo(userTaskId)
    }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits are found`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns emptyList()

        val result = getAuditUseCase.getAuditsForUserById(userTaskId)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)

        getAuditUseCase.getAuditsForUserById(userTaskId)

        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository list`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

        getAuditUseCase.getAuditsForUserById(userTaskId)

        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should return audits related to the correct user when multiple audits exist`() =
        runTest {
            coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

            val result = getAuditUseCase.getAuditsForUserById(userTaskId)

            assertThat(result).hasSize(1)
            assertThat(result.first().userId).isEqualTo(userTaskId)
        }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits match the userId`() = runTest {
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

        val result = getAuditUseCase.getAuditsForUserById(UUID.randomUUID())

        assertThat(result).isEmpty()
    }
}
