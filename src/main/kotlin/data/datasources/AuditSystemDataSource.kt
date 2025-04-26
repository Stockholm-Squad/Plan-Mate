package org.example.data.datasources

import org.example.logic.entities.AuditSystem
import org.example.logic.entities.AuditSystemType

interface AuditSystemDataSource {
    fun addAuditSystem(auditSystem: AuditSystem): Result<Boolean>
    fun getAuditSystemById(id: String): Result<AuditSystem>
    fun getAllAuditSystems(): Result<List<AuditSystem>>
    fun getAllAuditSystemsByType(type: AuditSystemType): Result<List<AuditSystem>>
    fun getAllAuditSystemsEntityId(entityId: String): Result<List<AuditSystem>>
}