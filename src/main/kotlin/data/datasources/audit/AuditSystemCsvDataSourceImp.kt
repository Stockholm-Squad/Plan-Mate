package org.example.data.datasources.audit

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.AuditSystemType

class AuditSystemCsvDataSourceImp: AuditSystemDataSource {
    override fun addAuditSystem(auditSystem: AuditSystem): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAuditSystemById(id: String): Result<AuditSystem> {
        TODO("Not yet implemented")
    }

    override fun getAllAuditSystems(): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }

    override fun getAllAuditSystemsByType(type: AuditSystemType): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }

    override fun getAllAuditSystemsEntityId(entityId: String): Result<List<AuditSystem>> {
        TODO("Not yet implemented")
    }
}