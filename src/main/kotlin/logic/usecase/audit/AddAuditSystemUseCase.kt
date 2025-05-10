package org.example.logic.usecase.audit

import org.example.data.utils.DateHandlerImp
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import org.example.logic.repository.AuditSystemRepository
import java.util.*

class AddAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository,
    ) {
    suspend fun addAuditsEntries(auditEntry: List<AuditSystem>): Boolean =
        auditSystemRepository.addAuditsEntries(auditEntry)

    suspend fun addEntityChangeHistory(
        userId: UUID,
        entityType: EntityType,
        entityId: UUID,
        description: String,
    ): Boolean {
        val auditEntry = AuditSystem(
            userId = userId,
            entityType = entityType,
            entityTypeId = entityId,
            description = description,
            dateTime = DateHandlerImp().getCurrentDateTime(),
        )
        return auditSystemRepository.addAuditsEntries(listOf(auditEntry))
    }
}