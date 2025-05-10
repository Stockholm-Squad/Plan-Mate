package org.example.logic.usecase.audit

import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.Audit
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditRepository

import java.util.*

class AddAuditUseCase(
    private val auditRepository: AuditRepository,
) {
    suspend fun addAuditsEntries(auditEntry: List<Audit>): Boolean =
        auditRepository.addAuditsEntries(auditEntry)

    suspend fun addEntityChangeHistory(
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
            dateTime = DateHandlerImp().getCurrentDateTime(),
        )
        return auditRepository.addAuditsEntries(listOf(auditEntry))
    }
}