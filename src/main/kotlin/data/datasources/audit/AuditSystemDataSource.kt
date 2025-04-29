package org.example.data.datasources.audit

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

interface AuditSystemDataSource {
    fun read(filePath: String) : Result<List<AuditSystem>>
    fun write(filePath: String, auditSystems: AuditSystem) : Result<Boolean>
}