package org.example.logic.repository

import logic.model.entities.AuditSystem

interface AuditSystemRepository {
    fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean>
    fun getAllAuditEntries(): Result<List<AuditSystem>>
    fun initializeDataInFile(auditSystem: List<AuditSystem>) : Result<Boolean>
}