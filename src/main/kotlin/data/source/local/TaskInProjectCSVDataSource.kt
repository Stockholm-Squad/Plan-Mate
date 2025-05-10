package org.example.data.source.local

import data.dto.TaskInProjectDto
import org.example.data.source.TaskInProjectDataSource

class TaskInProjectCSVDataSource : TaskInProjectDataSource {
    override suspend fun addTaskInProject(projectId: String, taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTasksInProject(): List<TaskInProjectDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto> {
        TODO("Not yet implemented")
    }
}