package org.example.data.source.remote.impl


import data.dto.AuditDto

import org.example.data.source.AuditDataSource
import org.litote.kmongo.coroutine.CoroutineCollection

class AuditMongoDataSource(
    private val auditsCollection: CoroutineCollection<AuditDto>
) : AuditDataSource {

    override suspend fun addAuditsEntries(auditSystem: List<AuditDto>): Boolean {
        return auditsCollection.insertMany(auditSystem).wasAcknowledged()
    }

    override suspend fun getAllAuditEntries(): List<AuditDto> {
        return auditsCollection.find().toList()
    }
}
