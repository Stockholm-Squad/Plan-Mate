package org.example.data.repo

import logic.model.entities.Task
import org.example.data.datasources.task.TaskDataSource
import org.example.logic.repository.TaskRepository

class TaskRepositoryImp(
    private val taskDataSource: TaskDataSource
) : TaskRepository {

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