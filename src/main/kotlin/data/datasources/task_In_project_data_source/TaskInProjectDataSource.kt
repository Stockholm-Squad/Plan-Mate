package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel
import org.example.data.models.ProjectStateModel


interface TaskInProjectDataSource {
    fun addTaskInProject(projectId: String, taskId: String): Boolean
    fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
    fun getAllTasksInProject(): List<TaskInProjectModel>
    fun getTaskInProjectByProjectId(projectId: String): ProjectStateModel
}