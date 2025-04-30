package logic.usecase.audit

import com.google.common.truth.Truth
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
    fun `addChangeInTask should return true when successfully added task changes`() {
        //given

        //When
        val result = manageAuditSystemUseCase.addChangeInTask()

        // then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `addChangeInProject should return true when successfully added project changes`(){
        // when
        val result = manageAuditSystemUseCase.addChangeInProject()

        // then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `getTaskChanges should return audit system for task when found`(){
        // when
        val result = manageAuditSystemUseCase.getTaskChanges()

        // then
        Truth.assertThat(result).hasSize(0)
    }

    @Test
    fun `getProjectChanges should return audit system for project when found`(){
        // when
        val result = manageAuditSystemUseCase.getProjectChanges()

        // then
        Truth.assertThat(result).hasSize(0)
    }

    @Test
    fun `getChangesByUser should return audit system when found`(){
        // when
        val result = manageAuditSystemUseCase.getChangesByUser()

        // then
        Truth.assertThat(result).hasSize(0)
    }


    @Test
    fun `clearChanges should return true when clear was successful`(){
        // when
        val result = manageAuditSystemUseCase.clearChanges()

        // then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun `clearChanges should return false when there is no data to clear`(){
        // when
        val result = manageAuditSystemUseCase.clearChanges()

        // then
        Truth.assertThat(result).isFalse()
    }
}