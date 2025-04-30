package data.datasources.audit

import logic.model.entities.AuditSystemType
import com.google.common.truth.Truth.assertThat
import io.kotest.mpp.file
import logic.model.entities.AuditSystem
import org.example.data.datasources.AuditSystemCsvDataSource
import org.example.data.datasources.PlanMateDataSource
import org.example.utils.createAuditSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemCsvDataSourceTest {

    private lateinit var auditSystemCsvDataSource: PlanMateDataSource<AuditSystem>
    private lateinit var tempFile: String

    @BeforeEach
    fun setUp() {
        tempFile = "audit_system.csv"
        auditSystemCsvDataSource = AuditSystemCsvDataSource(tempFile)
    }

    @Test
    fun `Write should return true when adding is valid`() {

        // Given
        val auditSystem = createAuditSystem(
            id = "1",
            auditSystemType = AuditSystemType.TASK,
            entityId = "123",
            changeDescription = "change description",
            changedBy = "Hamsa"
        )


        // When
        val result = auditSystemCsvDataSource.write(listOf(auditSystem))

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isTrue()
    }

    @Test
    fun `Write should return false when adding data with empty entityId`() {

        // Given
        val auditSystem = createAuditSystem(
            id = "1",
            auditSystemType = AuditSystemType.TASK,
            entityId = "", // empty ref to tasks
            changeDescription = "change description",
            changedBy = "Hamsa"
        )


        // When
        val result = auditSystemCsvDataSource.write(listOf(auditSystem))

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }

    @Test
    fun `Write should return false when adding data with empty changedBy`() {

        // Given
        val auditSystem = createAuditSystem(
            id = "1",
            auditSystemType = AuditSystemType.TASK,
            entityId = "123",
            changeDescription = "change description",
            changedBy = "" // empty user who do it
        )


        // When
        val result = auditSystemCsvDataSource.write(listOf(auditSystem))

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.getOrNull()).isFalse()
    }


    @Test
    fun `read should return all added audit systems`() {

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

        auditSystemCsvDataSource.write(auditSystem)

        // When
        val result = auditSystemCsvDataSource.read(tempFile)

        // Then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).hasSize(2)
    }
}