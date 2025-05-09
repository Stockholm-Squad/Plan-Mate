package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel
import org.example.data.models.ProjectStateModel


interface TaskInProjectDataSource {
   suspend fun addTaskInProject(projectId: String, taskId: String): Boolean
   suspend  fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
   suspend fun getAllTasksInProject(): List<TaskInProjectModel>
   suspend  fun getTaskInProjectByProjectId(projectId: String): ProjectStateModel
}