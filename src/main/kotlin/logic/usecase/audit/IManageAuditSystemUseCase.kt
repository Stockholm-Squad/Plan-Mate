package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem

interface IManageAuditSystemUseCase {
    fun getAllAuditSystems(): Result<List<AuditSystem>>
    fun getAuditSystemByID(auditId: String): Result<List<AuditSystem>>
    fun getTaskChangeLogsById(taskId: String): Result<List<AuditSystem>>
    fun getProjectChangeLogsById(projectId: String): Result<List<AuditSystem>>
    fun getUserChangeLogsByUsername(username: String): Result<List<AuditSystem>>
    fun recordAuditsEntries(auditEntry: List<AuditSystem>): Result<Boolean>
}
