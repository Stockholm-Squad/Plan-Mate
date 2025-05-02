package org.example.data.datasources.models.audit_system_data_source

import org.example.data.models.AuditSystem

interface IAuditSystemDataSource {
    fun read(): Result<List<AuditSystem>>
    fun overWrite(users: List<AuditSystem>): Result<Boolean>
    fun append(users: List<AuditSystem>): Result<Boolean>
}