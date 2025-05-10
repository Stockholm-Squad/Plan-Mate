package org.example.data.datasources

import data.dto.AuditSystemModel

interface IAuditSystemDataSource {
    suspend fun read(): List<AuditSystemModel>
    suspend fun overWrite(audits: List<AuditSystemModel>): Boolean
    suspend fun append(audits: List<AuditSystemModel>): Boolean
}