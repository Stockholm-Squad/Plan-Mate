package org.example.data.repo

import data.mapper.toDomainAuditSystemEntity
import org.example.data.datasources.models.audit_system_data_source.IAuditSystemDataSource
import org.example.data.models.AuditSystem
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: IAuditSystemDataSource
) : AuditSystemRepository {
    override fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.append(auditSystem)

    override fun getAllAuditEntries(): Result<List<logic.model.entities.AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { Result.success(it.map { it.toDomainAuditSystemEntity() }) },
            onFailure = { Result.failure(it) }
        )

    override fun initializeDataInFile(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.overWrite(auditSystem)
}