package data.source.remote.mongo


import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.AuditDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.AuditDataSource

class AuditMongoDataSource(
    private val auditsCollection: MongoCollection<AuditDto>,
) : AuditDataSource {

    override suspend fun addAudit(audit: AuditDto): Boolean =
        auditsCollection.insertOne(audit).insertedId != null


    override suspend fun getAllAudits(): List<AuditDto> = auditsCollection.find().toList()

}
