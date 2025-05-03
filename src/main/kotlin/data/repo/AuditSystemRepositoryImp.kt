package org.example.data.repo

import data.mapper.mapToAuditSystemEntity
import data.mapper.mapToAuditSystemModel
import logic.model.entities.AuditSystem
import org.example.data.datasources.audit_system_data_source.IAuditSystemDataSource
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: IAuditSystemDataSource,
) : AuditSystemRepository {
    override fun addAuditsEntries(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.append(auditSystem.map { it.mapToAuditSystemModel() })

    override fun getAllAuditEntries(): Result<List<AuditSystem>> =
        auditSystemDataSource.read().fold(
            onSuccess = { Result.success(it.mapNotNull { it.mapToAuditSystemEntity() }) },
            onFailure = { Result.failure(it) }
        )

    override fun initializeDataInFile(auditSystem: List<AuditSystem>): Result<Boolean> =
        auditSystemDataSource.overWrite(auditSystem.map { it.mapToAuditSystemModel() })
}