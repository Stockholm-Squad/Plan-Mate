package data.source.remote.mongo

import data.dto.TaskInProjectDto
import org.example.data.source.TaskInProjectDataSource
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class TaskInProjectMongoDataSource(
    private val taskInProjectCollection: CoroutineCollection<TaskInProjectDto>
) : TaskInProjectDataSource {

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
        val taskInProject = TaskInProjectDto(taskId, projectId)
        taskInProjectCollection.insertOne(taskInProject)
        return true
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {
        val result =
            taskInProjectCollection.deleteOne(
                TaskInProjectDto::projectId eq projectId,
                TaskInProjectDto::taskId eq taskId
            )
        return result.deletedCount > 0
    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> {
        return taskInProjectCollection.find().toList()
    }

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> {
        return taskInProjectCollection.find(TaskInProjectDto::projectId eq projectId).toList()
    }
}
