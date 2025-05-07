package org.example.data.datasources.mate_task_assignment_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
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
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (mateTasks.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(mateTasks)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == mateTasks.size
    }

    override suspend fun append(mateTasks: List<MateTaskAssignmentModel>): Boolean = withContext(Dispatchers.IO) {
        if (mateTasks.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(mateTasks)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == mateTasks.size
    }
}