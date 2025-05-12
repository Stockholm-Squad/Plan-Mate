package data.repo

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.repo.AuditRepositoryImp
import org.example.data.source.AuditDataSource
import org.example.logic.AuditNotAddedException
import org.example.logic.NoAuditsFoundException
import org.example.logic.repository.AuditRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows

import kotlin.test.Test

class AuditRepositoryImpTest {
    private lateinit var auditDataSource: AuditDataSource
    private lateinit var auditRepository: AuditRepository

    @BeforeEach
    fun setUp() {
        auditDataSource = mockk(relaxed = true)
        auditRepository = AuditRepositoryImp(auditDataSource)
    }

    @Test
    fun `addAudit() should return true when added successfully`() = runTest {
        // Given
        coEvery { auditDataSource.addAudit(auditDto) } returns true
        // When
        val result = auditRepository.addAudit(audit)
        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `addAudit() should return false when addition fails`() = runTest {
        // Given
        coEvery { auditDataSource.addAudit(auditDto) } returns false
        // When
        val result = auditRepository.addAudit(audit)
        // Then
        assertThat(result).isFalse()
    }

    @Test
    fun `addAudit() should throw exception when dataSource fails`() = runTest {
        // Given
        coEvery { auditDataSource.addAudit(auditDto) } throws Exception()
        // Then
        assertThrows<AuditNotAddedException>{
            auditRepository.addAudit(audit)
        }

    }
    @Test
    fun `getAllAudits() should return list of audits when dataSource succeeds`() = runTest {
        // Given
        coEvery { auditDataSource.getAllAudits() } returns listOf(auditDto)
        // When
        val result = auditRepository.getAllAudits()
        // Then
        assertThat(result).containsExactly(audit)
    }

    @Test
    fun `getAllAudits() should return empty list when dataSource returns nothing`() = runTest {
        // Given
        coEvery { auditDataSource.getAllAudits() } returns emptyList()
        // When
        val result = auditRepository.getAllAudits()
        // Then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getAllAudits() should throw exception when dataSource fails`() = runTest {
        // Given
        coEvery { auditDataSource.getAllAudits() } throws Exception()
        // Then
        assertThrows<NoAuditsFoundException> {
            auditRepository.getAllAudits()
        }
    }
}