package org.example.data.source

import data.dto.MateTaskAssignmentDto
import data.dto.TaskDto
import data.dto.TaskInProjectDto

interface TaskDataSource {
    suspend fun getAllTasks(): List<TaskDto>
    suspend fun addTask(task: TaskDto): Boolean
    suspend fun updateTask(task: TaskDto): Boolean
    suspend fun deleteTask(id: String): Boolean
    suspend fun getTasksInProject(projectId: String): List<TaskDto>
    suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto>
    suspend fun getAllTasksByUserName(username: String): List<TaskDto>

    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto>
    suspend fun getUsersMateTaskByUserName(username: String): List<MateTaskAssignmentDto>

    suspend fun addTaskInProject(projectId: String, taskId: String): Boolean
    suspend fun deleteTaskFromProject(projectId: String, taskId: String): Boolean
    suspend fun getAllTasksInProject(): List<TaskInProjectDto>
    suspend fun getTasksInProjectByProjectId(projectId: String): List<TaskInProjectDto>
}
