package org.example.data.datasources.audit_system_data_source

import org.example.data.models.AuditSystemModel

interface IAuditSystemDataSource {
    suspend fun read(): List<AuditSystemModel>
    suspend fun overWrite(users: List<AuditSystemModel>): Boolean
    suspend fun append(users: List<AuditSystemModel>): Boolean
}