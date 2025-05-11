package data.source.remote.mongo


import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.AuditDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.AuditDataSource

class AuditMongoDataSource(
    private val auditsCollection: MongoCollection<AuditDto>,
) : AuditDataSource {

    override suspend fun addAuditsEntries(auditSystem: List<AuditDto>): Boolean {
        return auditsCollection.insertMany(auditSystem).wasAcknowledged()
    }

    override suspend fun getAllAuditEntries(): List<AuditDto> {
        return auditsCollection.find().toList()
    }
}
