package org.example.data.datasources.task_data_source

import org.example.data.database.TASKS_COLLECTION_NAME
import org.example.data.models.TaskModel
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class TaskMongoDataSource(mongoDatabase: CoroutineDatabase) : TaskDataSource {

    private val collection = mongoDatabase.getCollection<TaskModel>(TASKS_COLLECTION_NAME)

    override suspend fun getAllTasks(): List<TaskModel> {
        return collection.find().toList()
    }

    override suspend fun addTask(task: TaskModel): Boolean {
        val result = collection.insertOne(task)
        return result.wasAcknowledged()
    }

    override suspend fun editTask(task: TaskModel): Boolean {
        val result = collection.replaceOne(TaskModel::id eq task.id, task)
        return result.modifiedCount > 0
    }

    override suspend fun deleteTask(id: String): Boolean {
        val result = collection.deleteOne(TaskModel::id eq id)
        return result.deletedCount > 0
    }

    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskModel> {
        return collection.find(TaskModel::id `in` taskIds).toList()
    }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskModel> {
        return collection.find(TaskModel::id `in` taskIds).toList()
    }
}
