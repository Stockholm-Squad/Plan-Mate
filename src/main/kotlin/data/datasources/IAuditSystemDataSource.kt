package org.example.data.datasources

import data.dto.AuditSystemDto

interface IAuditSystemDataSource {
    suspend fun read(): List<AuditSystemDto>
    suspend fun overWrite(audits: List<AuditSystemDto>): Boolean
    suspend fun append(audits: List<AuditSystemDto>): Boolean
}