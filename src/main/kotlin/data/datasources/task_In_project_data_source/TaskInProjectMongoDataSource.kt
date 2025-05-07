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

    override suspend fun overWrite(tasks: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (tasks.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(tasks)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == tasks.size
    }

    override suspend fun append(tasks: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        if (tasks.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(tasks)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == tasks.size
    }
}
