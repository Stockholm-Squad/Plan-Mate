package org.example.logic.usecase.audit

import logic.usecase.login.LoginUseCase
import org.example.logic.entities.EntityType
import org.example.logic.usecase.audit.utils.AuditDescriptionProvider
import java.util.*


class AuditServicesUseCase(
    private val addAuditUseCase: AddAuditUseCase,
    private val loginUseCase: LoginUseCase,
    private val auditDescriptionProvider: AuditDescriptionProvider
) {

    suspend fun addAuditForAddEntity(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String? = null
    ) {
        val auditDescription = auditDescriptionProvider.getAuditDescriptionForAdd(
            entityType = entityType,
            entityName = entityName,
            entityId = entityId,
            additionalInfo = additionalInfo ?: ""
        )
        val userId = loginUseCase.getCurrentUser()?.id
        if (userId == null) return
        addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
    }

    suspend fun addAuditForUpdateEntity(
        entityType: EntityType,
        existEntityName: String,
        newEntityName: String,
        entityId: UUID,
        newDescription: String? = null,
        newStateName: String? = null
    ) {
        val auditDescription = auditDescriptionProvider.getAuditDescriptionForUpdate(
            entityType = entityType,
            existEntityName = existEntityName,
            newEntityName = newEntityName,
            entityId = entityId,
            newDescription = newDescription ?: "",
            newStateName = newStateName ?: ""
        )
        val userId = loginUseCase.getCurrentUser()?.id
        if (userId == null) return
        addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
    }

    suspend fun addAuditForDeleteEntity(
        entityType: EntityType,
        entityName: String,
        entityId: UUID,
        additionalInfo: String? = null
    ) {
        val auditDescription = auditDescriptionProvider.getAuditDescriptionForDelete(
            entityType = entityType,
            entityName = entityName,
            entityId = entityId,
            additionalInfo = additionalInfo ?: ""
        )
        val userId = loginUseCase.getCurrentUser()?.id
        if (userId == null) return
        addAuditUseCase.addAudit(userId, entityType, entityId, auditDescription)
    }
}

