package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import java.util.UUID

interface IManageAuditSystemUseCase {
    fun getAuditsByEntityTypeId(entityId: UUID): Result<List<AuditSystem>>
    fun getAuditsByUserId(userId: UUID): Result<List<AuditSystem>>
    fun addAuditsEntries(auditEntry: List<AuditSystem>): Result<Boolean>
}
