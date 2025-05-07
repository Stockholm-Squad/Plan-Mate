package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase


class MateTaskAssignmentMongoDataSource(mongoDatabase: CoroutineDatabase) : IMateTaskAssignmentDataSource {

    private val collection =
        mongoDatabase.getCollection<MateTaskAssignmentModel>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun read(): List<MateTaskAssignmentModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(mateTasks: List<MateTaskAssignmentModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(mateTasks)
        true
    }

    override suspend fun append(mateTasks: List<MateTaskAssignmentModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(mateTasks)
        true
    }
}