package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.TaskDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.TaskDataSource
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class TaskMongoDataSource(
    private val taskCollection: MongoCollection<TaskDto>,
) : TaskDataSource {

    override suspend fun getAllTasks(): List<TaskDto> {
        return taskCollection.find().toList()
    }

    override suspend fun addTask(task: TaskDto): Boolean {
        val result = taskCollection.insertOne(task)
        return result.wasAcknowledged()
    }

    override suspend fun editTask(task: TaskDto): Boolean {
        val result = taskCollection.replaceOne(TaskDto::id eq task.id, task)
        return result.modifiedCount > 0
    }

    override suspend fun deleteTask(id: String): Boolean {
        val result = taskCollection.deleteOne(TaskDto::id eq id)
        return result.deletedCount > 0
    }

    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto> {
        return taskCollection.find(TaskDto::id `in` taskIds).toList()
    }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> {
        return taskCollection.find(TaskDto::id `in` taskIds).toList()
    }
}
