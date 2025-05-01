package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class ManageAuditSystemUseCase(
    private val auditSystemRepository: AuditSystemRepository
) : IManageAuditSystemUseCase {

    override fun getAllAuditSystems(): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    override fun getAuditSystemByID(auditId: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.id == auditId }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun getTaskChangeLogsById(taskId: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.entityId == taskId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun getProjectChangeLogsById(projectId: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.entityId == projectId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun getUserChangeLogsByUsername(username: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.changedBy == username }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun recordAuditsEntries(auditEntry: List<AuditSystem>): Result<Boolean> =
        auditSystemRepository.recordAuditsEntries(auditEntry).fold(
            onSuccess = {
                Result.success(true)
            },
            onFailure = { Result.failure(it) }
        )
}
