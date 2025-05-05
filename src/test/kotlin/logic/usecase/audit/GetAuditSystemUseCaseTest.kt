package logic.usecase.audit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import data.mapper.mapToAuditSystemModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import logic.models.entities.AuditSystem
import logic.models.entities.EntityType
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.GetAuditSystemUseCase
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.usecase.project.ManageProjectUseCase
import org.example.logic.usecase.task.ManageTasksUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class GetAuditSystemUseCaseTest {

    private lateinit var getAuditSystemUseCase: GetAuditSystemUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository
    private lateinit var manageProjectUseCase: ManageProjectUseCase
    private lateinit var getProjectsUseCase: GetProjectsUseCase
    private lateinit var manageTasksUseCase: ManageTasksUseCase

    private val taskAuditEntityUUID = UUID.randomUUID()
    private val taskAuditEntityTypeUUID = UUID.randomUUID()
    private val taskAuditEntityUserUUID = UUID.randomUUID()
    private val taskAuditEntity = AuditSystem(
        id = taskAuditEntityUUID,
        entityType = EntityType.TASK,
        entityTypeId = taskAuditEntityTypeUUID,
        description = "change from 'Open' to 'In Progress'",
        userId = taskAuditEntityUserUUID,
        dateTime = LocalDateTime(2025, 12, 19, 12, 0)
    )

    private val projectAuditEntityUUID = UUID.randomUUID()
    private val projectAuditEntityTypeUUID = UUID.randomUUID()
    private val projectAuditEntityUserUUID = UUID.randomUUID()
    private val projectAuditEntity = AuditSystem(
        id = projectAuditEntityUUID,
        entityType = EntityType.TASK,
        entityTypeId = projectAuditEntityTypeUUID,
        description = "change from 'Open' to 'In Progress'",
        userId = projectAuditEntityUserUUID,
        dateTime = LocalDateTime(2025, 12, 19, 12, 0)
    )
    private val projectAuditModel = projectAuditEntity.mapToAuditSystemModel()


    private val invalidTaskAuditEntityUUID = UUID.randomUUID()
    private val invalidTaskAuditEntityTypeUUID = UUID.randomUUID()
    private val invalidTaskAuditEntityUserUUID = UUID.randomUUID()
    private val invalidTaskAuditEntity = AuditSystem(
        id = invalidTaskAuditEntityUUID,
        entityType = EntityType.TASK,
        entityTypeId = invalidTaskAuditEntityTypeUUID,
        description = "change from 'Open' to 'In Progress'",
        userId = invalidTaskAuditEntityUserUUID,
        dateTime = LocalDateTime(2025, 12, 19, 12, 0)
    )
    private val invalidTaskAuditModel = invalidTaskAuditEntity.mapToAuditSystemModel()


    @BeforeEach
    fun setUp() {
        auditSystemRepository = mockk(relaxed = true)
        manageProjectUseCase = mockk(relaxed = true)
        manageTasksUseCase = mockk(relaxed = true)
        getAuditSystemUseCase = GetAuditSystemUseCase(auditSystemRepository, getProjectsUseCase, manageTasksUseCase)
    }



    @Test
    fun `getTaskChangeLogsById should return audit system for task when found`() {
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(listOf(taskAuditEntity))

        // when
        val result = getAuditSystemUseCase.getTaskAuditsByName("taskName")

        // then
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `getTaskChangeLogsById should return failure audit system for task when not found`() {
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = getAuditSystemUseCase.getTaskAuditsByName("taskName")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAuditSystemByID should return audit system for task when found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(listOf(taskAuditEntity))

        // when
        val result = getAuditSystemUseCase.getTaskAuditsByName("FoundTaskName")

        // then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getAuditSystemByID should return failure audit system for task when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = getAuditSystemUseCase.getTaskAuditsByName("notFoundTaskName")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getProjectChanges should return audit system for project when found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(listOf(projectAuditEntity))

        // when
        val result = getAuditSystemUseCase.getProjectAuditsByName("3")

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getProjectChanges should return failure audit system for project when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = getAuditSystemUseCase.getProjectAuditsByName("4")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getChangesByUser should return audit system when found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(listOf(projectAuditEntity))

        // when
        val result = getAuditSystemUseCase.getAuditsByUserId(UUID.randomUUID())

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getChangesByUser should return failure audit system when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = getAuditSystemUseCase.getAuditsByUserId(UUID.randomUUID())

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

}