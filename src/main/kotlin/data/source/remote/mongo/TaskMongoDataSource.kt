package data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.MateTaskAssignmentDto
import data.dto.TaskDto
import data.dto.TaskInProjectDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.TaskDataSource

import org.litote.kmongo.eq
import org.litote.kmongo.`in`

class TaskMongoDataSource(
    private val taskCollection: MongoCollection<TaskDto>,
    private val mateTaskAssignmentCollection: MongoCollection<MateTaskAssignmentDto>,
    private val taskInProjectCollection: MongoCollection<TaskInProjectDto>,
) : TaskDataSource {

    override suspend fun getAllTasks(): List<TaskDto> = taskCollection.find().toList()

    override suspend fun addTask(task: TaskDto): Boolean = taskCollection.insertOne(task).insertedId != null

    override suspend fun updateTask(task: TaskDto): Boolean =
        taskCollection.replaceOne(TaskDto::id eq task.id, task).modifiedCount > 0

    override suspend fun deleteTask(id: String): Boolean =
        taskCollection.deleteOne(TaskDto::id eq id).deletedCount > 0

    override suspend fun getTasksInProject(projectId: String): List<TaskDto> =
        getTasksInProjectByProjectId(projectId).map { it.taskId }.let { taskIds ->
            getTasksByIds(taskIds)
        }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> =
        taskCollection.find(TaskDto::id `in` taskIds).toList()

    override suspend fun getAllTasksByUserName(username: String): List<TaskDto> =
        getUsersMateTaskByUserName(username).map { it.taskId }
            .let { mateTaskAssignments -> getTasksByIds(mateTaskAssignments) }

    override suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentCollection.find(MateTaskAssignmentDto::taskId eq taskId).toList()

    override suspend fun getUsersMateTaskByUserName(username: String): List<MateTaskAssignmentDto> =
        mateTaskAssignmentCollection.find(MateTaskAssignmentDto::username eq username).toList()

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean =
        taskInProjectCollection.insertOne(TaskInProjectDto(taskId, projectId)).insertedId != null

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean =
        taskInProjectCollection.deleteOne(
            Filters.and(
                Filters.eq(TaskInProjectDto::projectId.name, projectId),
                Filters.eq(TaskInProjectDto::taskId.name, taskId)
            )
        ).deletedCount > 0

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> =
        taskInProjectCollection.find().toList()

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> =
        taskInProjectCollection.find(TaskInProjectDto::projectId eq projectId).toList()
}