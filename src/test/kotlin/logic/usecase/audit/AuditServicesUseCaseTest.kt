package logic.usecase.audit

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import logic.usecase.login.LoginUseCase
import org.example.logic.usecase.audit.AddAuditUseCase
import org.example.logic.usecase.audit.AuditServicesUseCase
import org.example.logic.usecase.audit.utils.AuditDescriptionProvider
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class AuditServicesUseCaseTest {

    private lateinit var addAuditUseCase: AddAuditUseCase
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var auditDescriptionProvider: AuditDescriptionProvider
    private lateinit var auditServicesUseCase: AuditServicesUseCase

    @BeforeEach
    fun setUp() {
        addAuditUseCase = mockk(relaxed = true)
        loginUseCase = mockk(relaxed = true)
        auditDescriptionProvider = mockk(relaxed = true)
        auditServicesUseCase = AuditServicesUseCase(addAuditUseCase, loginUseCase, auditDescriptionProvider)
    }

    @Test
    fun `addAuditForAddEntity() should return true when adding entity`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForAdd(
                entityType,
                entityName,
                entityId,
                additionalInfo
            )
        } returns addedDescription
        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, addedDescription) } returns true

        // When & Then
        auditServicesUseCase.addAuditForAddEntity(entityType, entityName, entityId, additionalInfo)
    }

    @Test
    fun `addAuditForAddEntity() should return true when additionalInfo is empty`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForAdd(
                entityType,
                entityName,
                entityId,
                ""
            )
        } returns addedDescription
        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, addedDescription) } returns true

        // When & Then
        auditServicesUseCase.addAuditForAddEntity(entityType, entityName, entityId, "")
    }

    @Test
    fun `addAuditForAddEntity() should use default values when additionalInfo is not passed`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForAdd(
                entityType,
                entityName,
                entityId,
                ""
            )
        } returns addedDescription
        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, addedDescription) } returns true

        // When & Then
        auditServicesUseCase.addAuditForAddEntity(entityType, entityName, entityId)
    }

    @Test
    fun `addAuditForAddEntity() should not call addAudit when user is not found`() = runTest {
        // Given
        every { loginUseCase.getCurrentUser() } returns null

        // When
        auditServicesUseCase.addAuditForAddEntity(entityType, entityName, entityId)

        // Then
        coVerify(exactly = 0) { addAuditUseCase.addAudit(any(), any(), any(), any()) }
    }

    @Test
    fun `addAuditForUpdateEntity() should call addAudit with correct data`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForUpdate(
                entityType,
                existingName,
                newName,
                entityId,
                newDescription,
                newStateName
            )
        } returns updateDescription
        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, updateDescription) } returns true

        // When & Then
        auditServicesUseCase.addAuditForUpdateEntity(
            entityType, existingName,
            newName,
            entityId,
            newDescription,
            newStateName
        )
    }

    @Test
    fun `addAuditForUpdateEntity() should use default values when newDescription and newStateName are not passed`() =
        runTest {
            // Given
            coEvery {
                auditDescriptionProvider.getAuditDescriptionForUpdate(
                    entityType = entityType,
                    existEntityName = existingName,
                    newEntityName = newName,
                    entityId = entityId,
                    newDescription = "",
                    newStateName = ""
                )
            } returns updateDescription

            coEvery { loginUseCase.getCurrentUser() } returns user
            coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, updateDescription) } returns true

            // When & Then
            auditServicesUseCase.addAuditForUpdateEntity(
                entityType = entityType,
                existEntityName = existingName,
                newEntityName = newName,
                entityId = entityId
            )

        }

    @Test
    fun `addAuditForUpdateEntity() should not call addAudit when user is not found`() = runTest {
        // Given
        every { loginUseCase.getCurrentUser() } returns null

        // When
        auditServicesUseCase.addAuditForUpdateEntity(
            entityType = entityType,
            existEntityName = existingName,
            newEntityName = newName,
            entityId = entityId
        )

        // Then
        coVerify(exactly = 0) { addAuditUseCase.addAudit(any(), any(), any(), any()) }
    }

    @Test
    fun `addAuditForDeleteEntity() should return true when adding entity`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForDelete(
                entityType,
                entityName,
                entityId,
                additionalInfo
            )
        } returns deletedDescription
        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery { addAuditUseCase.addAudit(userId, entityType, entityId, deletedDescription) } returns true

        // When & Then
        auditServicesUseCase.addAuditForDeleteEntity(entityType, entityName, entityId, additionalInfo)
    }

    @Test
    fun `addAuditForDeleteEntity() should use default value when additionalInfo is not passed`() = runTest {
        // Given
        coEvery {
            auditDescriptionProvider.getAuditDescriptionForDelete(
                entityType = entityType,
                entityName = entityName,
                entityId = entityId,
                additionalInfo = ""
            )
        } returns deletedDescription

        coEvery { loginUseCase.getCurrentUser() } returns user
        coEvery {
            addAuditUseCase.addAudit(userId, entityType, entityId, deletedDescription)
        } returns true

        // When
        auditServicesUseCase.addAuditForDeleteEntity(
            entityType = entityType,
            entityName = entityName,
            entityId = entityId
        )
    }

    @Test
    fun `addAuditForDeleteEntity() should not call addAudit when user is not found`() = runTest {
        // Given
        every { loginUseCase.getCurrentUser() } returns null

        // When
        auditServicesUseCase.addAuditForDeleteEntity(entityType, entityName, entityId, additionalInfo)

        // Then
        coVerify(exactly = 0) { addAuditUseCase.addAudit(any(), any(), any(), any()) }
    }

}