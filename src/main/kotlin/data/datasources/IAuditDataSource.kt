package org.example.data.datasources

import data.dto.AuditDto

interface IAuditDataSource {
    suspend fun read(): List<AuditDto>
    suspend fun overWrite(audits: List<AuditDto>): Boolean
    suspend fun append(audits: List<AuditDto>): Boolean
}