package org.example.logic.usecase.audit

import org.example.logic.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class AddAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository,
    ) {
    suspend fun addAuditsEntries(auditEntry: List<AuditSystem>): Boolean =
        auditSystemRepository.addAuditsEntries(auditEntry)
}