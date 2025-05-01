package org.example.data.repo

import logic.model.entities.AuditSystem
import logic.model.entities.AuditSystemType

import org.example.data.datasources.PlanMateDataSource
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: PlanMateDataSource<AuditSystem>
) : AuditSystemRepository {
    override fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.append(auditSystem).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )


    override fun getAllAuditEntries(): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )

    override fun initializeDataInFile(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.overWrite(auditSystem).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )



}