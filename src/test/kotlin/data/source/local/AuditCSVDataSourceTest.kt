package data.source.local

import com.google.common.truth.Truth.assertThat
import data.dto.AuditDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.AuditDataSource
import org.example.data.source.local.AuditCSVDataSource
import org.example.data.source.local.csv_reader_writer.IReaderWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import utils.buildAuditModel

class AuditCSVDataSourceTest {

    private lateinit var readerWriter: IReaderWriter<AuditDto>
    private lateinit var dataSource: AuditDataSource

    private val sampleAudit1 = buildAuditModel()
    private val sampleAudit2 = buildAuditModel(id = "f3a85f64-5717-4562-b3fc-2c963f66bfa5")

    @BeforeEach
    fun setup() {
        readerWriter = mockk()
        dataSource = AuditCSVDataSource(readerWriter)
    }

    @Test
    fun `addAudit should append to CSV and return true when audit added`() = runTest {
        //Given
        coEvery { readerWriter.append(listOf(sampleAudit1)) } returns true

        //When
        val result = dataSource.addAudit(sampleAudit1)

        //Then
        assertThat(result).isTrue()
        coVerify { readerWriter.append(listOf(sampleAudit1)) }
    }

    @Test
    fun `addAudit should return false when append fails`() = runTest {
        //Given
        coEvery { readerWriter.append(listOf(sampleAudit1)) } returns false

        //When
        val result = dataSource.addAudit(sampleAudit1)

        //Then
        assertThat(result).isFalse()
        coVerify { readerWriter.append(listOf(sampleAudit1)) }
    }

    @Test
    fun `getAllAudits should return list of audits when called`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns listOf(sampleAudit1, sampleAudit2)

        //When
        val result = dataSource.getAllAudits()

        //Then
        assertThat(result).containsExactly(sampleAudit1, sampleAudit2)
    }

    @Test
    fun `getAllAudits should return empty list when no audits exist`() = runTest {
        //Given
        coEvery { readerWriter.read() } returns emptyList()

        //When
        val result = dataSource.getAllAudits()

        //Then
        assertThat(result).isEmpty()
    }
}