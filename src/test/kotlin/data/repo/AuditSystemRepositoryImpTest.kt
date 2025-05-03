package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.data.datasources.audit_system_data_source.AuditSystemCsvDataSource
import org.example.data.repo.AuditSystemRepositoryImp
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.createAuditSystemEntity
import utils.createAuditSystemModel
import java.util.*

class AuditSystemRepositoryImpTest {

    private lateinit var auditSystemRepositoryImp: AuditSystemRepositoryImp
    private lateinit var auditSystemDataSource: AuditSystemCsvDataSource

    private val auditListModel = listOf(
        createAuditSystemModel(
            id = "1",
            entityType = EntityType.TASK,
            entityId = "123",
            changeDescription = "Changed something",
            userId = "12"
        )
    )
    private val auditListEntity = listOf(
        createAuditSystemEntity(
            entityType = EntityType.TASK,
            changeDescription = "Changed something",
        )
    )

    @BeforeEach
    fun setUp() {
        auditSystemDataSource = mockk(relaxed = true)
        auditSystemRepositoryImp = AuditSystemRepositoryImp(auditSystemDataSource)
    }


    @Test
    fun `recordAuditsEntries returns success when append succeeds`() {
        //given
        every { auditSystemDataSource.append(auditListModel) } returns Result.success(true)
        //when
        val result = auditSystemRepositoryImp.addAuditsEntries(auditListEntity)
        //then
        assertThat(result.isSuccess).isTrue()
        verify { auditSystemDataSource.append(auditListModel) }
    }

    @Test
    fun `recordAuditsEntries returns failure when append fails`() {
        //given
        every { auditSystemDataSource.append(auditListModel) } returns Result.failure(Exception("Append failed"))
        //when
        val result = auditSystemRepositoryImp.addAuditsEntries(auditListEntity)
        //then
        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.append(auditListModel) }
    }

    @Test
    fun `getAllAuditEntries returns success when read succeeds`() {
        //given
        every { auditSystemDataSource.read() } returns Result.success(auditListModel)
        //when
        val result = auditSystemRepositoryImp.getAllAuditEntries()
        //then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(auditListModel)
        verify { auditSystemDataSource.read() }
    }

    @Test
    fun `getAllAuditEntries returns failure when read fails`() {
        //given
        every { auditSystemDataSource.read() } returns Result.failure(Exception("Read failed"))
        //when
        val result = auditSystemRepositoryImp.getAllAuditEntries()
        //then
        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.read() }
    }

    @Test
    fun `initializeDataInFile returns success when overwrite succeeds`() {
        //given
        every { auditSystemDataSource.overWrite(auditListModel) } returns Result.success(true)
        //when
        val result = auditSystemRepositoryImp.initializeDataInFile(auditListEntity)
        //then
        assertThat(result.isSuccess).isTrue()
        verify { auditSystemDataSource.overWrite(auditListModel) }
    }

    @Test
    fun `initializeDataInFile returns failure when overwrite fails`() {
        //given
        every { auditSystemDataSource.overWrite(auditListModel) } returns Result.failure(Exception("Overwrite failed"))
        //when
        val result = auditSystemRepositoryImp.initializeDataInFile(auditListEntity)
        //then
        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.overWrite(auditListModel) }
    }
}
