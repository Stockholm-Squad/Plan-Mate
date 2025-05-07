package org.example.data.datasources.task_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.TASKS_COLLECTION_NAME
import org.example.data.models.TaskModel
import org.litote.kmongo.coroutine.CoroutineDatabase


class TaskMongoDataSource(mongoDatabase: CoroutineDatabase) : ITaskDataSource {

    private val collection = mongoDatabase.getCollection<TaskModel>(TASKS_COLLECTION_NAME)

    override suspend fun read(): List<TaskModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(tasks: List<TaskModel>): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun append(tasks: List<TaskModel>): Boolean = withContext(Dispatchers.IO) {
        if (tasks.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(tasks)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == tasks.size
    }
}
