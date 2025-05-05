package org.example.data.datasources.task_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.models.TaskModel


class TaskMongoDataSource() : ITaskDataSource {

    private val collection = MongoSetup.database.getCollection<TaskModel>("tasks")

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
