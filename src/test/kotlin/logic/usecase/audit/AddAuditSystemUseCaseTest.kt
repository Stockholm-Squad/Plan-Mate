package logic.usecase.audit

import com.google.common.truth.Truth
import data.mapper.mapToAuditSystemModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AddAuditSystemUseCaseTest {
    private lateinit var addAuditSystemUseCase: AddAuditSystemUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository

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

    private val taskAuditModel = taskAuditEntity.mapToAuditSystemModel()

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
        addAuditSystemUseCase = AddAuditSystemUseCase(auditSystemRepository)
    }

    @Test
    fun `recordAuditsEntries should return true when successfully added task changes`() {
        //given
        every { auditSystemRepository.addAuditsEntries(listOf(taskAuditEntity)) } returns Result.success(true)

        //When
        val result = addAuditSystemUseCase.addAuditsEntries(listOf(taskAuditEntity))

        // then
        Truth.assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `recordAuditsEntries should return false when successfully added task changes`() {
        //given
        every { auditSystemRepository.addAuditsEntries(listOf(invalidTaskAuditEntity)) } returns Result.failure(Exception("error"))

        //When
        val result = addAuditSystemUseCase.addAuditsEntries(listOf(invalidTaskAuditEntity))

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }
}