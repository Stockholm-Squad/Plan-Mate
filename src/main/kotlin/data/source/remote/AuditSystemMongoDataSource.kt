package org.example.data.source.remote

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.utils.AUDITS_COLLECTION_NAME
import org.example.data.datasources.IAuditSystemDataSource
import data.dto.AuditSystemDto
import org.litote.kmongo.coroutine.CoroutineDatabase


class AuditSystemMongoDataSource(mongoDatabase: CoroutineDatabase) : IAuditSystemDataSource {

    private val collection = mongoDatabase.getCollection<AuditSystemDto>(AUDITS_COLLECTION_NAME)

    override suspend fun read(): List<AuditSystemDto> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(audits: List<AuditSystemDto>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (audits.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }

    override suspend fun append(audits: List<AuditSystemDto>): Boolean = withContext(Dispatchers.IO) {
        if (audits.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }
}
