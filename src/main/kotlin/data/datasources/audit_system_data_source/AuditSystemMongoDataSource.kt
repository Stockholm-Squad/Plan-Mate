package org.example.data.datasources.audit_system_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.models.AuditSystemModel
import org.example.data.utils.AUDITS_COLLECTION_NAME


class AuditSystemMongoDataSource() : IAuditSystemDataSource {

    private val collection = MongoSetup.database.getCollection<AuditSystemModel>(AUDITS_COLLECTION_NAME)

    override suspend fun read(): List<AuditSystemModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(audits: List<AuditSystemModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(audits)
        true
    }

    override suspend fun append(audits: List<AuditSystemModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(audits)
        true
    }
}
