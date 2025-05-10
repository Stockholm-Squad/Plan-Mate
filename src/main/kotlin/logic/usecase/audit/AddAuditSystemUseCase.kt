package org.example.logic.usecase.audit

import org.example.logic.entities.AuditSystem
import org.example.logic.repository.AuditRepository

class AddAuditSystemUseCase(
    private val auditRepository: AuditRepository,
    ) {
    suspend fun addAuditsEntries(auditEntry: List<AuditSystem>): Boolean =
        auditRepository.addAuditsEntries(auditEntry)
}