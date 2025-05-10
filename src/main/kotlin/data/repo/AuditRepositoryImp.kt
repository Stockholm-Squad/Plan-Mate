package org.example.data.repo

import data.mapper.mapToAuditEntity
import data.mapper.mapToAuditModel
import org.example.data.datasources.IAuditDataSource
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.AuditSystemNotAddedException
import org.example.logic.NoAuditsFoundedException
import org.example.logic.entities.AuditSystem
import org.example.logic.repository.AuditRepository

class AuditRepositoryImp(
    private val auditSystemDataSource: IAuditDataSource,
) : AuditRepository {
    override suspend fun addAuditsEntries(auditSystem: List<AuditSystem>): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.append(auditSystem.map { it.mapToAuditModel() })
            },
            onFailure = { throw AuditSystemNotAddedException() }
        )


    override suspend fun getAllAuditEntries(): List<AuditSystem> =
        executeSafelyWithContext(
            onSuccess = {
                auditSystemDataSource.read().mapNotNull { it.mapToAuditEntity() }
            },
            onFailure = { throw NoAuditsFoundedException() }
        )


}