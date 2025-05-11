package org.example.data.source.local

import data.dto.AuditDto
import org.example.data.source.AuditDataSource

class AuditCSVDataSource : AuditDataSource {
    override suspend fun addAuditsEntries(audit: List<AuditDto>): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllAuditEntries(): List<AuditDto> {
        TODO("Not yet implemented")
    }
}