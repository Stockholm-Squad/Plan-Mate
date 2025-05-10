package org.example.data.source

import data.dto.AuditDto

interface AuditDataSource {
    suspend fun addAuditsEntries(auditSystem: List<AuditDto>): Boolean
    suspend fun getAllAuditEntries(): List<AuditDto>
}