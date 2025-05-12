package data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.TaskInProjectDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.TaskInProjectDataSource
import org.litote.kmongo.eq

class TaskInProjectMongoDataSource(
    private val taskInProjectCollection: MongoCollection<TaskInProjectDto>,
) : TaskInProjectDataSource {

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