package org.example.data.datasources.audit_system_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.AUDITS_COLLECTION_NAME
import org.example.data.models.AuditSystemModel
import org.litote.kmongo.coroutine.CoroutineDatabase


class AuditSystemMongoDataSource(mongoDatabase: CoroutineDatabase) : IAuditSystemDataSource {

    private val collection = mongoDatabase.getCollection<AuditSystemModel>(AUDITS_COLLECTION_NAME)

    override suspend fun read(): List<AuditSystemModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(audits: List<AuditSystemModel>): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun append(audits: List<AuditSystemModel>): Boolean = withContext(Dispatchers.IO) {
        if (audits.isEmpty()) {
            return@withContext true // nothing to append, considered success
        }
        val insertResult: InsertManyResult = collection.insertMany(audits)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == audits.size
    }
}
