package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import logic.model.entities.AuditSystem
import logic.model.entities.EntityType
import org.example.data.datasources.PlanMateDataSource
import org.example.data.repo.AuditSystemRepositoryImp
import utils.createAuditSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditSystemRepositoryImpTest {

    private lateinit var auditSystemRepositoryImp: AuditSystemRepositoryImp
    private lateinit var auditSystemDataSource: PlanMateDataSource<AuditSystem>

    private val auditList = listOf(
        createAuditSystem(
            id = "1",
            entityType = EntityType.TASK,
            entityId = "123",
            changeDescription = "Changed something",
            changedBy = "Admin"
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
        every { auditSystemDataSource.append(auditList) } returns Result.success(true)
        //when
        val result = auditSystemRepositoryImp.addAuditsEntries(auditList)
        //then
        assertThat(result.isSuccess).isTrue()
        verify { auditSystemDataSource.append(auditList) }
    }

    @Test
    fun `recordAuditsEntries returns failure when append fails`() {
        //given
        every { auditSystemDataSource.append(auditList) } returns Result.failure(Exception("Append failed"))
        //when
        val result = auditSystemRepositoryImp.addAuditsEntries(auditList)
        //then
        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.append(auditList) }
    }

    @Test
    fun `getAllAuditEntries returns success when read succeeds`() {
        //given
        every { auditSystemDataSource.read() } returns Result.success(auditList)
        //when
        val result = auditSystemRepositoryImp.getAllAuditEntries()
        //then
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(auditList)
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
        every { auditSystemDataSource.overWrite(auditList) } returns Result.success(true)
        //when
        val result = auditSystemRepositoryImp.initializeDataInFile(auditList)
        //then
        assertThat(result.isSuccess).isTrue()
        verify { auditSystemDataSource.overWrite(auditList) }
    }

    @Test
    fun `initializeDataInFile returns failure when overwrite fails`() {
        //given
        every { auditSystemDataSource.overWrite(auditList) } returns Result.failure(Exception("Overwrite failed"))
        //when
        val result = auditSystemRepositoryImp.initializeDataInFile(auditList)
        //then
        assertThat(result.isFailure).isTrue()
        verify { auditSystemDataSource.overWrite(auditList) }
    }
}
