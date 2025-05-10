package org.example.data.source.remote


import data.dto.TaskInProjectDto
import org.example.data.utils.MATE_TASK_ASSIGNMENT_COLLECTION_NAME
import org.example.data.source.TaskInProjectDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq


class TaskInProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : TaskInProjectDataSource {

    private val collection = mongoDatabase.getCollection<TaskInProjectDto>(MATE_TASK_ASSIGNMENT_COLLECTION_NAME)

    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
        val taskInProject = TaskInProjectDto(taskId, projectId)
        collection.insertOne(taskInProject)
        return true
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {
        val result =
            collection.deleteOne(TaskInProjectDto::projectId eq projectId, TaskInProjectDto::taskId eq taskId)
        return result.deletedCount > 0
    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> {
        return collection.find().toList()
    }

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> {
        return collection.find(TaskInProjectDto::projectId eq projectId).toList()
    }
}
