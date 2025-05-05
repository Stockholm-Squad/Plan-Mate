package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.utils.TASK_IN_PROJECT_COLLECTION_NAME

class TaskInProjectMongoDataSource() : ITaskInProjectDataSource {

    private val collection = MongoSetup.database.getCollection<TaskInProjectModel>(TASK_IN_PROJECT_COLLECTION_NAME)


    override suspend fun read(): List<TaskInProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(users)
        true
    }

    override suspend fun append(users: List<TaskInProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(users)
        true
    }
}
