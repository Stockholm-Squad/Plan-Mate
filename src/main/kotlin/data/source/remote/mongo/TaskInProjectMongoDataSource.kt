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

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
        val taskInProject = TaskInProjectDto(taskId, projectId)
        taskInProjectCollection.insertOne(taskInProject)
        return true
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {
        val deleteFilter = Filters.and(
            Filters.eq(TaskInProjectDto::projectId.name, projectId),
            Filters.eq(TaskInProjectDto::taskId.name, taskId)
        )
        val result =
            taskInProjectCollection.deleteOne(deleteFilter)
        return result.deletedCount > 0
    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> {
        return taskInProjectCollection.find().toList()
    }

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> {
        return taskInProjectCollection.find(TaskInProjectDto::projectId eq projectId).toList()
    }
}
