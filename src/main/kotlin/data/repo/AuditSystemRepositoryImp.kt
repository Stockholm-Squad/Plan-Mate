package org.example.data.repo

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType
import org.example.data.datasources.audit.AuditSystemDataSource
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: AuditSystemDataSource
) : AuditSystemRepository {

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