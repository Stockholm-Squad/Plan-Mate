package org.example.data.datasources.models.audit_system_data_source

import org.example.data.models.AuditSystemModel

interface IAuditSystemDataSource {
    fun read(): Result<List<AuditSystemModel>>
    fun overWrite(users: List<AuditSystemModel>): Result<Boolean>
    fun append(users: List<AuditSystemModel>): Result<Boolean>
}