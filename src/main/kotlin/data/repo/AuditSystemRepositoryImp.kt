package org.example.data.repo

import data.mapper.mapToAuditSystemEntity
import data.mapper.mapToAuditSystemModel
import org.example.logic.entities.AuditSystem
import org.example.logic.AuditExceptions
import org.example.data.datasources.audit_system_data_source.IAuditSystemDataSource
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.AuditSystemRepository

class AuditSystemRepositoryImp(
    private val auditSystemDataSource: IAuditSystemDataSource,
) : AuditSystemRepository {
    override suspend fun addAuditsEntries(auditSystem: List<AuditSystem>): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.append(auditSystem.map { it.mapToAuditSystemModel() })
            },
            onFailure = { throw AuditExceptions.AuditSystemNotAddedException() }
        )



    override suspend fun getAllAuditEntries(): List<AuditSystem> =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.read().mapNotNull { it.mapToAuditSystemEntity() }
            },
            onFailure = { throw AuditExceptions.NoAuditsFoundedException() }
        )


}