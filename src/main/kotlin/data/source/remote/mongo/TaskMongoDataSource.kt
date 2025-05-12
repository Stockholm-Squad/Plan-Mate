package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.TaskDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.MateTaskAssignmentDataSource
import org.example.data.source.TaskDataSource
import org.example.data.source.TaskInProjectDataSource
import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class TaskMongoDataSource(
    private val taskCollection: MongoCollection<TaskDto>,
    private val mateTaskAssignmentDataSource: MateTaskAssignmentDataSource,
    private val taskInProjectDataSource: TaskInProjectDataSource,
) : TaskDataSource {

    override suspend fun getAllTasks(): List<TaskDto> = taskCollection.find().toList()

    override suspend fun addTask(task: TaskDto): Boolean = taskCollection.insertOne(task).insertedId != null

    override suspend fun updateTask(task: TaskDto): Boolean =
        taskCollection.replaceOne(TaskDto::id eq task.id, task).modifiedCount > 0

    override suspend fun deleteTask(id: String): Boolean =
        taskCollection.deleteOne(TaskDto::id eq id).deletedCount > 0

    override suspend fun getTasksInProject(projectId: String): List<TaskDto> =
        taskInProjectDataSource.getTasksInProjectByProjectId(projectId).map { it.taskId }.let { taskIds ->
            getTasksByIds(taskIds)
        }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        taskCollection.find(TaskDto::id `in` taskIds).toList()

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean =
        taskInProjectDataSource.addTaskInProject(projectId, taskId)

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean =
        taskInProjectDataSource.deleteTaskFromProject(projectId = projectId, taskId = taskId)

    override suspend fun getAllTasksByUserName(username: String): List<TaskDto> =
        mateTaskAssignmentDataSource.getUsersMateTaskByUserName(username).map { it.taskId }
            .let { mateTaskAssignments -> getTasksByIds(mateTaskAssignments) }
}