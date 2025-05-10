package org.example.data.source.remote

import org.example.data.utils.TASKS_COLLECTION_NAME
import data.dto.TaskDto
import org.example.data.source.TaskDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class TaskMongoDataSource(mongoDatabase: CoroutineDatabase) : TaskDataSource {

    private val collection = mongoDatabase.getCollection<TaskDto>(TASKS_COLLECTION_NAME)

    override suspend fun getAllTasks(): List<TaskDto> {
        return collection.find().toList()
    }

    override suspend fun addTask(task: TaskDto): Boolean {
        val result = collection.insertOne(task)
        return result.wasAcknowledged()
    }

    override suspend fun editTask(task: TaskDto): Boolean {
        val result = collection.replaceOne(TaskDto::id eq task.id, task)
        return result.modifiedCount > 0
    }

    override suspend fun deleteTask(id: String): Boolean {
        val result = collection.deleteOne(TaskDto::id eq id)
        return result.deletedCount > 0
    }

    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto> {
        return collection.find(TaskDto::id `in` taskIds).toList()
    }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> {
        return collection.find(TaskDto::id `in` taskIds).toList()
    }
}
