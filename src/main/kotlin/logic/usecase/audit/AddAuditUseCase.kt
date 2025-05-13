package org.example.logic.usecase.audit

import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditRepository
import org.example.logic.utils.DateHandler

import java.util.*

class AddAuditUseCase(
    private val auditRepository: AuditRepository,
    private val dateHandler: DateHandler
) {

    suspend fun addAudit(
        userId: UUID,
        entityType: EntityType,
        entityId: UUID,
        description: String,
    ): Boolean {
        val auditEntry = Audit(
            userId = userId,
            entityType = entityType,
            entityTypeId = entityId,
            description = description,
            dateTime = dateHandler.getCurrentDateTime()
        )
        return auditRepository.addAudit(auditEntry)
    }
}