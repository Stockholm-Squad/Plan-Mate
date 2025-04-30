package org.example.logic.usecase.audit

import logic.model.entities.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class ManageAuditSystemUseCase(private val auditSystemRepository: AuditSystemRepository) {

    fun getAllAuditSystems() : Result<List<AuditSystem>> =
        auditSystemRepository.getAllAuditEntries().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    fun getTaskChangeLogsById(taskId: Int) : Result<List<AuditSystem>> =
        auditSystemRepository.getTaskChangeLogsById(taskId).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    fun getProjectChangeLogsById(projectId: Int) : Result<List<AuditSystem>> =
        auditSystemRepository.getProjectChangeLogsById(projectId).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    fun getUserChangeLogsByUsername(username: String) : Result<List<AuditSystem>> =
        auditSystemRepository.getUserChangeLogsByUsername(username).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    fun recordAuditEntry(auditEntry: AuditSystem) : Result<Boolean> =
        auditSystemRepository.recordAuditEntry(auditEntry).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )



}
