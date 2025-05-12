package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditRepository
import org.example.logic.usecase.audit.GetAuditUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertTrue

class GetAuditUseCaseTest {

    private lateinit var auditRepository: AuditRepository
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageTasksUseCase: ManageTasksUseCase
    private lateinit var getAuditUseCase: GetAuditUseCase

    private val projectName = "Project1"
    private val taskName = "Task1"
    private val dateTime = DateHandlerImp().getCurrentDateTime()

    private val auditProject = Audit(
        id = UUID.fromString("f3a85f64-5717-4562-b3fc-2c963f66bfa1"),
        entityType = EntityType.PROJECT,
        entityTypeId = UUID.fromString("e3a85f64-5717-4562-b3fc-2c963f66dabc"),
        description = "Added Project",
        userId = UUID.fromString("a3a85f64-5717-4562-b3fc-2c963f66abc1"),
        dateTime = dateTime
    )

    private val auditTask = Audit(
        id = UUID.fromString("5fa85f64-5717-4562-b3fc-2c963f66bfa2"),
        entityType = EntityType.TASK,
        entityTypeId = UUID.fromString("c3a85f64-5717-4562-b3fc-2c963f66abcd"),
        description = "Added Task",
        userId = UUID.fromString("8fa85f64-5717-4562-b3fc-2c963f66bfa3"),
        dateTime = dateTime
    )

    private val auditTask1 = Audit(
        id = UUID.fromString("9fa85f64-5717-4562-b3fc-2c963f66bfa4"),
        entityType = EntityType.TASK,
        entityTypeId = UUID.fromString("c3a85f64-5717-4562-b3fc-2c963f66abcd"),
        description = "Added Task",
        userId = UUID.fromString("7fa85f64-5717-4562-b3fc-2c963f66bfa5"),
        dateTime = dateTime
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
        // Given
        coEvery { auditRepository.getAllAudits() } returns listOf(auditProject)
        coEvery { getProjectsUseCase.getProjectByName(projectName) } returns mockk {
            every { id } returns auditProject.entityTypeId
        }

        // When
        val result = getAuditUseCase.getAuditsForProjectByName(projectName)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(auditProject.entityTypeId)
    }

    @Test
    fun `getAuditsForProjectByName() should return an empty list if no audits are found`() = runTest {
        // Given
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
        val taskName = "Task1"
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)
        coEvery { manageTasksUseCase.getAllTasks() } returns listOf(mockk {
            every { id } returns auditTask.entityTypeId
            every { name } returns taskName
        })

        // When
        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().entityTypeId).isEqualTo(auditTask.entityTypeId)
    }

    @Test
    fun `getAuditsForTaskByName() should return an empty list if no audits are found`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns emptyList()
        coEvery { manageTasksUseCase.getAllTasks() } returns emptyList()

        // When
        val result = getAuditUseCase.getAuditsForTaskByName(taskName)

        // Then
        assertThat(result).isEmpty()
    }

    /////////////////////////////////////////////
    @Test
    fun `getAuditsForUserById() should return audits related to the user`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)

        // When
        val result = getAuditUseCase.getAuditsForUserById(auditTask.userId)

        // Then
        assertThat(result).isNotEmpty()
        assertThat(result.first().userId).isEqualTo(auditTask.userId)
    }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits are found`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns emptyList()

        // When
        val result = getAuditUseCase.getAuditsForUserById(auditTask.userId)

        // Then
        assertTrue { result.isEmpty() }
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask)

        // When
        getAuditUseCase.getAuditsForUserById(auditTask.userId)

        // Then
        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should call getAllAudits from the repository list`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

        // When
        getAuditUseCase.getAuditsForUserById(auditTask.userId)

        // Then
        coVerify { auditRepository.getAllAudits() }
    }

    @Test
    fun `getAuditsForUserById() should return audits related to the correct user when multiple audits exist`() =
        runTest {
            // Given
            coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

            // When
            val result = getAuditUseCase.getAuditsForUserById(auditTask.userId)

            // Then
            assertThat(result).hasSize(1)
            assertThat(result.first().userId).isEqualTo(auditTask.userId)
        }

    @Test
    fun `getAuditsForUserById() should return an empty list if no audits match the userId`() = runTest {
        // Given
        coEvery { auditRepository.getAllAudits() } returns listOf(auditTask, auditTask1)

        // When
        val result = getAuditUseCase.getAuditsForUserById(UUID.randomUUID()) // A non-matching userId

        // Then
        assertTrue { result.isEmpty() }
    }

}
