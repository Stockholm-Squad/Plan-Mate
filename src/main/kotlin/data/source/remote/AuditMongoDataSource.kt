package org.example.data.source.remote

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.dto.AuditDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.IAuditDataSource
import org.litote.kmongo.coroutine.CoroutineCollection

class AuditMongoDataSource(
    private val auditsCollection: CoroutineCollection<AuditDto>
) : IAuditDataSource {

    override suspend fun read(): List<AuditDto> = withContext(Dispatchers.IO) {
        auditsCollection.find().toList()
    }

    override suspend fun overWrite(audits: List<AuditDto>): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun append(audits: List<AuditDto>): Boolean = withContext(Dispatchers.IO) {
        if (audits.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = auditsCollection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }
}
