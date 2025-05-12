package org.example.logic.repository

import org.example.logic.entities.Audit

interface AuditRepository {
    suspend fun addAudit(audit: Audit): Boolean
    suspend fun getAllAudits(): List<Audit>
}