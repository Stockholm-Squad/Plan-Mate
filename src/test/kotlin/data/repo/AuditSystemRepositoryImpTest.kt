package data.repo

import com.google.common.truth.Truth.assertThat
import data.mapper.mapToAuditSystemEntity
import data.mapper.mapToAuditSystemModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import org.example.data.source.local.AuditSystemCsvDataSource
import data.dto.AuditSystemModel
import org.example.data.repo.AuditSystemRepositoryImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AuditSystemRepositoryImpTest {

    private lateinit var auditSystemRepositoryImp: AuditSystemRepositoryImp
    private lateinit var auditSystemDataSource: AuditSystemCsvDataSource

    private lateinit var auditListModel: List<AuditSystemModel>
    private lateinit var auditListEntity: List<AuditSystem>

    @BeforeEach
    fun setUp() {
        auditSystemDataSource = mockk(relaxed = true)
        auditSystemRepositoryImp = AuditSystemRepositoryImp(auditSystemDataSource)

        val id = UUID.randomUUID()
        val entityId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val createdAt = LocalDateTime.parse("2020-04-04T00:00:00.000")

        auditListEntity = listOf(
            AuditSystem(
                id = id,
                entityType = EntityType.TASK,
                entityTypeId = entityId,
                userId = userId,
                description = "Changed something",
                dateTime = createdAt
            )
        )

        auditListModel = listOf(
            AuditSystemModel(
                id = id.toString(),
                entityType = "TASK",
                entityTypeId = entityId.toString(),
                userId = userId.toString(),
                description = "Changed something",
                dateTime = "2020-04-04T00:00:00.000"
            )
        )
    }


    @Test
    fun `recordAuditsEntries returns success when append succeeds`() {
        val expectedModels = auditListEntity.map { it.mapToAuditSystemModel() }
        every { auditSystemDataSource.append(expectedModels) } returns Result.success(true)

        val result = auditSystemRepositoryImp.addAuditsEntries(auditListEntity)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `recordAuditsEntries returns failure when append fails`() {
        val expectedModels = auditListEntity.map { it.mapToAuditSystemModel() }
        every { auditSystemDataSource.append(expectedModels) } returns Result.failure(Exception("Append failed"))

        val result = auditSystemRepositoryImp.addAuditsEntries(auditListEntity)

        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `getAllAuditEntries returns success when read succeeds`() {
        every { auditSystemDataSource.read() } returns Result.success(auditListModel)

        val result = auditSystemRepositoryImp.getAllAuditEntries()

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(auditListModel.mapNotNull { it.mapToAuditSystemEntity() })
    }

    @Test
    fun `getAllAuditEntries returns failure when read fails`() {
        every { auditSystemDataSource.read() } returns Result.failure(Exception("Read failed"))

        val result = auditSystemRepositoryImp.getAllAuditEntries()

        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.read() }
    }

    @Test
    fun `initializeDataInFile returns success when overwrite succeeds`() {
        val expectedModels = auditListEntity.map { it.mapToAuditSystemModel() }
        every { auditSystemDataSource.overWrite(expectedModels) } returns Result.success(true)

        val result = auditSystemRepositoryImp.initializeDataInFile(auditListEntity)

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `initializeDataInFile returns failure when overwrite fails`() {
        val expectedModels = auditListEntity.map { it.mapToAuditSystemModel() }
        every { auditSystemDataSource.overWrite(expectedModels) } returns Result.failure(Exception("Overwrite failed"))

        val result = auditSystemRepositoryImp.initializeDataInFile(auditListEntity)

        assertThat(result.isFailure).isTrue()
    }

}
