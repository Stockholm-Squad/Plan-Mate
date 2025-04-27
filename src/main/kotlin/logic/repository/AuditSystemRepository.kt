package org.example.logic.repository

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

interface AuditSystemRepository {
    fun addAuditSystem(auditSystem: AuditSystem): Result<Boolean>
    fun getAuditSystemById(id: String): Result<AuditSystem>
    fun getAllAuditSystems(): Result<List<AuditSystem>>
    fun getAllAuditSystemsByType(type: AuditSystemType): Result<List<AuditSystem>>
    fun getAllAuditSystemsEntityId(entityId: String): Result<List<AuditSystem>>
}