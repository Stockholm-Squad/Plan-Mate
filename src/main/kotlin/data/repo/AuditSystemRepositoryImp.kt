package org.example.data.repo

import org.example.data.datasources.AuditSystemDataSource
import org.example.logic.entities.AuditSystem
import org.example.logic.entities.AuditSystemType
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