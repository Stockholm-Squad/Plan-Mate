package logic.usecase.audit

import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ManageAuditSystemUseCaseTest {

    private lateinit var manageAuditSystemUseCase: ManageAuditSystemUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository

    @BeforeEach
    fun setUp() {
        auditSystemRepository = mockk(relaxed = true)
        manageAuditSystemUseCase = ManageAuditSystemUseCase(auditSystemRepository)
    }

    @Test
    fun `recordAuditsEntries should return true when successfully added task changes`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = EntityType.TASK,
                entityTypeId = UUID.fromString("2"),
                description = "change from 'Open' to 'In Progress'",
                userId = UUID.fromString("00000000-0000-0000-0000-00000000abcd"),
                dateTime = LocalDateTime(2025, 12, 19, 12, 0)
            )
        )
        every { auditSystemRepository.addAuditsEntries(data) } returns Result.success(true)

        //When
        val result = manageAuditSystemUseCase.addAuditsEntries(data)

        // then
        Truth.assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `recordAuditsEntries should return false when successfully added task changes`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = EntityType.TASK,
                entityTypeId = UUID.fromString(""),
                description = "change from 'Open' to 'In Progress'",
                userId = UUID.fromString("00000000-0000-0000-0000-00000000abcd"),
                dateTime = LocalDateTime(2025, 12, 19, 12, 0)
            )
        )
        every { auditSystemRepository.addAuditsEntries(data) } returns Result.failure(Exception("error"))

        //When
        val result = manageAuditSystemUseCase.addAuditsEntries(data)

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }



    @Test
    fun `getTaskChangeLogsById should return audit system for task when found`() {
        val data = listOf(
            AuditSystem(
                entityType = "TASK",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(data)

        // when
        val result = manageAuditSystemUseCase.getTaskChangeLogsById("3")

        // then
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `getTaskChangeLogsById should return failure audit system for task when not found`() {
        val data = listOf(
            AuditSystem(
                entityType = "TASK",
                entityId = "4",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = manageAuditSystemUseCase.getTaskChangeLogsById("3")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAuditSystemByID should return audit system for task when found`() {
        //given
        val data = listOf(
            AuditSystem(
                id = "test",
                entityType = "TASK",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(data)

        // when
        val result = manageAuditSystemUseCase.getAuditSystemByID("test")

        // then
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getAuditSystemByID should return failure audit system for task when not found`() {
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))

        // when
        val result = manageAuditSystemUseCase.getAuditSystemByID("test12")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getProjectChanges should return audit system for project when found`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = "PROJECT",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(data)
        // when
        val result = manageAuditSystemUseCase.getAuditsByEntityTypeId("3")

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getProjectChanges should return failure audit system for project when not found`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = "PROJECT",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))
        // when
        val result = manageAuditSystemUseCase.getAuditsByEntityTypeId("4")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getChangesByUser should return audit system when found`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = "PROJECT",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(data)
        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId("mano")

        // then
        Truth.assertThat(result.getOrNull()).hasSize(1)
    }

    @Test
    fun `getChangesByUser should return failure audit system when not found`() {
        //given
        val data = listOf(
            AuditSystem(
                entityType = "PROJECT",
                entityId = "3",
                changeDescription = "SAFAFGA",
                changedBy = "mano",
                dateTime = "15/12/2005"
            )
        )
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(Exception("error"))
        // when
        val result = manageAuditSystemUseCase.getAuditsByUserId("mano1")

        // then
        Truth.assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAllAuditSystems should return success result`(){
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.success(emptyList())
        //when
        val result = manageAuditSystemUseCase.getAllAuditSystems()
        //then
        assertThat(result.isSuccess).isTrue()
        verify(exactly = 1) { auditSystemRepository.getAllAuditEntries() }
    }

    @Test
    fun `getAllAuditSystems should return failure result`(){
        //given
        every { auditSystemRepository.getAllAuditEntries() } returns Result.failure(exception = Exception("error"))
        //when
        val result = manageAuditSystemUseCase.getAllAuditSystems()
        //then
        assertThat(result.isFailure).isTrue()
    }
}