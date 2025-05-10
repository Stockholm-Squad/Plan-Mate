package org.example.logic.usecase.audit

import kotlinx.datetime.LocalDateTime
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.EntityType
import java.util.*

class LogAuditUseCase(
    private val addAuditSystemUseCase: AddAuditSystemUseCase,
) {

    suspend fun logAudit(
        userId: UUID,
        entityType: EntityType,
        entityId: UUID,
        description: String,
        timestamp: LocalDateTime,
    ): Boolean {


        val auditEntry = AuditSystem(
            userId = userId,
            entityType = entityType,
            entityTypeId = entityId,
            description = description,
            dateTime = timestamp,
        )
        return addAuditSystemUseCase.addAuditsEntries(listOf(auditEntry))
    }
}