package org.example.data.datasources

import org.example.data.models.AuditSystemModel

interface IAuditSystemDataSource {
    suspend fun read(): List<AuditSystemModel>
    suspend fun overWrite(audits: List<AuditSystemModel>): Boolean
    suspend fun append(audits: List<AuditSystemModel>): Boolean
}