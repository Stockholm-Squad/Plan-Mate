package org.example.data.repo

import data.mapper.mapToAuditEntity
import data.mapper.mapToAuditModel
import org.example.data.source.AuditDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.AuditSystemNotAddedException
import org.example.logic.NoAuditsFoundedException
import org.example.logic.entities.Audit
import org.example.logic.repository.AuditRepository

class AuditRepositoryImp(
    private val auditDataSource: AuditDataSource,
) : AuditRepository {
    override suspend fun addAuditsEntries(audit: List<Audit>): Boolean =
        tryToExecute(
            {
                auditDataSource.addAuditsEntries(audit.map { it.mapToAuditModel() })
            },
            onSuccess = { success -> success },
            onFailure = { throw AuditSystemNotAddedException() }
        )


    override suspend fun getAllAuditEntries(): List<Audit> = tryToExecute(
        { auditDataSource.getAllAuditEntries().mapNotNull { it.mapToAuditEntity() } },
        onSuccess = { listOfAudits -> listOfAudits },
        onFailure = { throw NoAuditsFoundedException() })
}

