package org.example.data.repo

import data.mapper.mapToAuditEntity
import data.mapper.mapToAuditModel
import org.example.data.source.AuditDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.AuditNotAddedException
import org.example.logic.NoAuditsFoundException
import org.example.logic.entities.Audit
import org.example.logic.repository.AuditRepository

class AuditRepositoryImp(
    private val auditDataSource: AuditDataSource,
) : AuditRepository {
    override suspend fun addAudit(audit: Audit): Boolean = tryToExecute(
        function = { auditDataSource.addAudit(audit.mapToAuditModel()) },
        onSuccess = { isAdded -> isAdded },
        onFailure = { throw AuditNotAddedException() }
    )


    override suspend fun getAllAudits(): List<Audit> = tryToExecute(
        function = { auditDataSource.getAllAudits().mapNotNull { it.mapToAuditEntity() } },
        onSuccess = { listOfAudits -> listOfAudits },
        onFailure = { throw NoAuditsFoundException() })

}

