package org.example.logic.repository

import logic.models.entities.AuditSystem

interface AuditSystemRepository {
    suspend fun addAuditsEntries(auditSystem: List<AuditSystem>): Boolean
    suspend fun getAllAuditEntries(): List<AuditSystem>
}