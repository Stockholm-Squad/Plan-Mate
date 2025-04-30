package org.example.logic.repository

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

interface AuditSystemRepository {
    fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean>
    fun getAllAuditEntries(): Result<List<AuditSystem>>
    fun getTaskChangeLogsById(taskId: Int): Result<List<AuditSystem>>
    fun getProjectChangeLogsById(projectId: Int): Result<List<AuditSystem>>
    fun getUserChangeLogsByUsername(username: String): Result<List<AuditSystem>>
}