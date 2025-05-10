package org.example.data.source.remote

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.dto.AuditSystemDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.IAuditSystemDataSource
import org.litote.kmongo.coroutine.CoroutineCollection

class AuditSystemMongoDataSource(
    private val auditsCollection: CoroutineCollection<AuditSystemDto>
) : IAuditSystemDataSource {

    override suspend fun read(): List<AuditSystemDto> = withContext(Dispatchers.IO) {
        auditsCollection.find().toList()
    }

    override suspend fun overWrite(audits: List<AuditSystemDto>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = auditsCollection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (audits.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = auditsCollection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }

    override suspend fun append(audits: List<AuditSystemDto>): Boolean = withContext(Dispatchers.IO) {
        if (audits.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = auditsCollection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }
}
