package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class ManageAuditSystemUseCase(private val auditSystemRepository: AuditSystemRepository) {

    fun getAllAuditSystems(): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    fun getTaskChangeLogsById(taskId: Int): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.entityId == taskId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    fun getProjectChangeLogsById(projectId: Int): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.entityId == projectId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    fun getUserChangeLogsByUsername(username: String): Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = {
                val result = it.filter { it.changedBy == username }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    fun recordAuditEntry(auditEntry: List<AuditSystem>): Result<Boolean> =
        auditSystemRepository.recordAuditsEntries(auditEntry).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )


}
