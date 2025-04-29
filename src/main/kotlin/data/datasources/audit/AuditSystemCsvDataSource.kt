package org.example.data.datasources.audit

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

class AuditSystemCsvDataSource : AuditSystemDataSource {
    override fun read(filePath: String): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }

    override fun write(filePath: String, auditSystems: AuditSystem): Result<Boolean> {
        TODO("Not yet implemented")
    }

}