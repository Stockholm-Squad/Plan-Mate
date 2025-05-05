package org.example.data.datasources.project_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.models.TaskModel

class ProjectMongoDataSource() : ITaskDataSource {

    private val collection = MongoSetup.database.getCollection<TaskModel>("projects")


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
