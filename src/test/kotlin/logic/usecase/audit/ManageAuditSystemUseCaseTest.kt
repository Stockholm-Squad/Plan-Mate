package logic.usecase.audit

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import logic.model.entities.AuditSystemType
import org.example.logic.repository.AuditSystemRepository
import org.example.logic.usecase.audit.ManageAuditSystemUseCase
import org.example.utils.createAuditSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ManageAuditSystemUseCaseTest {

    private lateinit var manageAuditSystemUseCase: ManageAuditSystemUseCase
    private lateinit var auditSystemRepository: AuditSystemRepository

    @BeforeEach
    fun setUp() {
        auditSystemRepository = mockk(relaxed = true)
        manageAuditSystemUseCase = ManageAuditSystemUseCase(auditSystemRepository)
    }

    @Test
    fun `addAuditSystem should return true when successfully added audit`() {

    }

    @Test
    fun `getAuditSystemById should return audit system when found`(){

    }

    @Test
    fun `getAllAuditSystemsEntityId should return only matching entity id`() {
        // Given
        val auditSystem = listOf(
            createAuditSystem(
                id = "1",
                auditSystemType = AuditSystemType.TASK,
                entityId = "123",
                changeDescription = "change description",
                changedBy = "Hamsa"
            ),
            createAuditSystem(
                id = "2",
                auditSystemType = AuditSystemType.TASK,
                entityId = "123",
                changeDescription = "change description",
                changedBy = "Hamsa"
            )
        )
        auditSystem.forEach { auditSystemRepository.addAuditSystem(it) }

        // When
        val result = auditSystemRepository.getAllAuditSystemsEntityId("1")

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(1)
        assertThat(result.getOrNull()?.first()?.entityId).isEqualTo("1")
    }

    @Test
    fun `getAllAuditSystemsByType should return only matching type`() {

        // Given
        val auditSystem = listOf(
            createAuditSystem(
                id = "1",
                auditSystemType = AuditSystemType.TASK,
                entityId = "123",
                changeDescription = "change description",
                changedBy = "Hamsa"
            ),
            createAuditSystem(
                id = "2",
                auditSystemType = AuditSystemType.TASK,
                entityId = "123",
                changeDescription = "change description",
                changedBy = "Hamsa"
            )
        )
        auditSystem.forEach { auditSystemCsvDataSource.addAuditSystem(it) }

        // When
        val result = auditSystemCsvDataSource.getAllAuditSystemsByType(AuditSystemType.TASK)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
        assertThat(result.getOrNull()?.first()?.auditSystemType).isEqualTo(AuditSystemType.TASK)
    }


}