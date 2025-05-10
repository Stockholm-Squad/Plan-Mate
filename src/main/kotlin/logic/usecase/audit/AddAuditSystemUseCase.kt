package org.example.logic.usecase.audit

import org.example.logic.entities.Audit
import org.example.logic.repository.AuditRepository

class AddAuditSystemUseCase(
    private val auditRepository: AuditRepository,
    ) {
    suspend fun addAuditsEntries(auditEntry: List<Audit>): Boolean =
        auditRepository.addAuditsEntries(auditEntry)
}