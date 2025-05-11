package data.source.remote.mongo


import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.AuditDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.AuditDataSource

class AuditMongoDataSource(
    private val auditsCollection: MongoCollection<AuditDto>,
) : AuditDataSource {

    override suspend fun addAudit(audit: AuditDto): Boolean =
        auditsCollection.insertOne(audit).wasAcknowledged()


    override suspend fun getAllAudits(): List<AuditDto> =
        auditsCollection.find().toList()
}
