package org.example.logic.repository

import org.example.logic.entities.Audit

interface AuditRepository {
    suspend fun addAudit(audit: List<Audit>): Boolean
    suspend fun getAllAudits(): List<Audit>
}