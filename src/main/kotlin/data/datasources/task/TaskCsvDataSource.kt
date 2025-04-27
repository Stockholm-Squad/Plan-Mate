package org.example.data.datasources.task

import logic.model.entities.Task

class TaskCsvDataSource : TaskDataSource {
    override fun getTaskById(id: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override fun addTask(task: Task): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editTask(task: Task): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteTask(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getAllTasksByProjectId(projectId: String): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getAllTasksByUserId(userId: String): Result<List<Task>> {
        TODO("Not yet implemented")
    }
}