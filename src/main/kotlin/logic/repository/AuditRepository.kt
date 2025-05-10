package org.example.logic.repository

import org.example.logic.entities.AuditSystem

interface AuditRepository {
    suspend fun addAuditsEntries(auditSystem: List<AuditSystem>): Boolean
    suspend fun getAllAuditEntries(): List<AuditSystem>
}