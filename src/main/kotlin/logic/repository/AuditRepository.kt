package org.example.logic.repository

import org.example.logic.entities.Audit

interface AuditRepository {
    suspend fun addAuditsEntries(audit: List<Audit>): Boolean
    suspend fun getAllAuditEntries(): List<Audit>
}