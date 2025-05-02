package org.example.logic.repository
import org.example.data.models.AuditSystem

interface AuditSystemRepository {
    fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean>
    fun getAllAuditEntries(): Result<List<logic.model.entities.AuditSystem>>
    fun initializeDataInFile(auditSystem: List<AuditSystem>) : Result<Boolean>
}