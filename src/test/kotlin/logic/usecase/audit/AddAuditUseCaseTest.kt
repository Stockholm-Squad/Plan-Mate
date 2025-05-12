package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditRepository
import org.example.logic.usecase.audit.AddAuditUseCase
import org.junit.jupiter.api.BeforeEach
import java.util.*
import kotlin.test.Test


class AddAuditUseCaseTest {
    private lateinit var auditRepository: AuditRepository
    private lateinit var addAuditUseCase: AddAuditUseCase
    val entityType: EntityType = EntityType.TASK
    val entityTypeId: UUID = UUID.fromString("e2c592e6-2618-405f-96b4-03bba272416d")
    val userId: UUID = UUID.fromString("5df64cdc-824b-4b93-ac3a-5fda9528466c")
    val description = "Task created"

    @BeforeEach
    fun setUp() {
        auditRepository = mockk(relaxed = true)
        addAuditUseCase = AddAuditUseCase(auditRepository)
    }

    @Test
    fun `addAudit() should return true when audit is successfully added`() = runTest {
        // Given
        coEvery { auditRepository.addAudit(any()) } returns true

        // When
        val result = addAuditUseCase.addAudit(userId, entityType, entityTypeId, description)

        // Then
        assertThat(result).isTrue()
    }


    @Test
    fun `addAudit() should return false when audit repository returns false`() = runTest {
        // Given
        coEvery { auditRepository.addAudit(any()) } returns false

        // When
        val result = addAuditUseCase.addAudit(userId, entityType, entityTypeId, description)

        // Then
        assertThat(result).isFalse()
    }
}
