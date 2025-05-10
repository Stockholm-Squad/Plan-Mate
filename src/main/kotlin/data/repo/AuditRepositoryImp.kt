package org.example.data.repo

import data.mapper.mapToAuditEntity
import data.mapper.mapToAuditModel
import org.example.data.datasources.IAuditDataSource
import org.example.data.source.AuditDataSource
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.AuditSystemNotAddedException
import org.example.logic.NoAuditsFoundedException
import org.example.logic.entities.Audit
import org.example.logic.repository.AuditRepository

class AuditRepositoryImp(
    private val auditSystemDataSource: AuditDataSource,
) : AuditRepository {
    override suspend fun addAuditsEntries(audit: List<Audit>): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.addAuditsEntries(audit.map { it.mapToAuditModel() })
            },
            onFailure = { throw AuditSystemNotAddedException() }
        )


    override suspend fun getAllAuditEntries(): List<Audit> =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.getAllAuditEntries().mapNotNull { it.mapToAuditEntity() }
            },
            onFailure = { throw NoAuditsFoundedException() }
        )


}