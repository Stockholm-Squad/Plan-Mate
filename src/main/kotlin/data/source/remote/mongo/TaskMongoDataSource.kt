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

    override suspend fun getAllTasks(): List<TaskDto> =
        taskCollection.find().toList()

    override suspend fun addTask(task: TaskDto): Boolean =
        taskCollection.insertOne(task).insertedId != null

    override suspend fun updateTask(task: TaskDto): Boolean =
        taskCollection.replaceOne(TaskDto::id eq task.id, task).modifiedCount > 0

    override suspend fun deleteTask(id: String): Boolean =
        taskCollection.deleteOne(TaskDto::id eq id).deletedCount > 0

    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto> =
        taskCollection.find(TaskDto::id `in` taskIds).toList()

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        taskCollection.find(TaskDto::id `in` taskIds).toList()
}