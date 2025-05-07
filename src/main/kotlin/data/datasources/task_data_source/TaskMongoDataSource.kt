package org.example.data.datasources.task_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.models.TaskModel
import org.example.data.database.TASKS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase


class TaskMongoDataSource(mongoDatabase: CoroutineDatabase) : ITaskDataSource {

    private val collection = mongoDatabase.getCollection<TaskModel>(TASKS_COLLECTION_NAME)

    override suspend fun read(): List<TaskModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<TaskModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(users)
        true
    }

    override suspend fun append(users: List<TaskModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(users)
        true
    }
}
