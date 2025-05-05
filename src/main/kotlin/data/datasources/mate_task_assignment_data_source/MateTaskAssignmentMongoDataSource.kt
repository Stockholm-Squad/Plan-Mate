package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.utils.MATE_TASK_ASSIGNMENT_COLLECTION_NAME


class MateTaskAssignmentMongoDataSource() : IMateTaskAssignmentDataSource {

    private val collection =
        MongoSetup.database.getCollection<MateTaskAssignmentModel>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun read(): List<MateTaskAssignmentModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(audits: List<MateTaskAssignmentModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(audits)
        true
    }

    override suspend fun append(audits: List<MateTaskAssignmentModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(audits)
        true
    }
}