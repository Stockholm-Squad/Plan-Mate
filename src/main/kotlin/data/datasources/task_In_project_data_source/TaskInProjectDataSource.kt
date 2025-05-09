package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel
import java.util.UUID

interface TaskInProjectDataSource {
    fun addTaskInProject(projectId: UUID, taskId: UUID): Boolean
    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Boolean
    fun getAllTasksInProject(): List<TaskInProjectModel>
}