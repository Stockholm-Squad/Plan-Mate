package org.example.data.repo

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

import org.example.data.datasources.PlanMateDataSource
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: PlanMateDataSource<AuditSystem>
) : AuditSystemRepository {
    override fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.write(auditSystem).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )


    override fun getAllAuditEntries(): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    override fun getTaskChangeLogsById(taskId: Int): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { auditList ->
                val result = auditList.filter { it.entityId == taskId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )


    override fun getProjectChangeLogsById(projectId: Int): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { auditList ->
                val result = auditList.filter { it.entityId == projectId.toString() }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

    override fun getUserChangeLogsByUsername(username: String): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { auditList ->
                val result = auditList.filter { it.changedBy == username }
                Result.success(result)
            },
            onFailure = { Result.failure(it) }
        )

}