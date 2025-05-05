package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class AddAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository,
    ) {
    fun addAuditsEntries(auditEntry: List<AuditSystem>): Result<Boolean> =
        auditSystemRepository.addAuditsEntries(auditEntry)
}