package org.example.data.datasources.task_In_project_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.models.TaskInProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.TASK_IN_PROJECT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class TaskInProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : ITaskInProjectDataSource {

    private val collection = mongoDatabase.getCollection<TaskInProjectModel>(TASK_IN_PROJECT_COLLECTION_NAME)


    override suspend fun read(): List<TaskInProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (users.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(users)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == users.size
    }

    override suspend fun append(users: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        if (users.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(users)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == users.size
    }
}
