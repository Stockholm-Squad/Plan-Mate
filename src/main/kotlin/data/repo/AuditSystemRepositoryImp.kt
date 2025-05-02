package org.example.data.repo

import data.mapper.AuditSystemMapper
import logic.model.entities.AuditSystem
import org.example.data.datasources.models.audit_system_data_source.IAuditSystemDataSource
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: IAuditSystemDataSource,
    private val auditSystemMapper: AuditSystemMapper
) : AuditSystemRepository {
    override fun recordAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.append(auditSystem.map(auditSystemMapper::mapToAuditSystemModel))

    override fun getAllAuditEntries(): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { Result.success(it.map(auditSystemMapper::mapToAuditSystemEntity)) },
            onFailure = { Result.failure(it) }
        )

    override fun initializeDataInFile(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.overWrite(auditSystem.map(auditSystemMapper::mapToAuditSystemModel))
}