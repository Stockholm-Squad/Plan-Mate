package org.example.data.source

import data.dto.AuditDto

interface AuditDataSource {
    suspend fun addAudit(audit: List<AuditDto>): Boolean
    suspend fun getAllAudits(): List<AuditDto>
}