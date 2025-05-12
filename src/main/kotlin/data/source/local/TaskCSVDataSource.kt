package org.example.data.source.local

import data.dto.TaskDto
import org.example.data.source.TaskDataSource

class TaskCSVDataSource : TaskDataSource {
    override suspend fun getAllTasks(): List<TaskDto> {
        TODO("Not yet implemented")
    }

    override suspend fun addTask(task: TaskDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateTask(task: TaskDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTask(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksInProject(taskIds: List<String>): List<TaskDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasksByIds(taskIds: List<String>): List<TaskDto> {
        TODO("Not yet implemented")
    }
}