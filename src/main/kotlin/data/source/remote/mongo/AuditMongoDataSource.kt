package data.source.remote.mongo


import data.dto.AuditDto

import org.example.data.source.AuditDataSource
import org.litote.kmongo.coroutine.CoroutineCollection

class AuditMongoDataSource(
    private val auditsCollection: CoroutineCollection<AuditDto>
) : AuditDataSource {

    override suspend fun addAudit(audit: AuditDto): Boolean {
        return auditsCollection.insertOne(audit).wasAcknowledged()
    }

    override suspend fun getAllAudits(): List<AuditDto> {
        return auditsCollection.find().toList()
    }
}
