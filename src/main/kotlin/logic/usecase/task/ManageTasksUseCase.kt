package org.example.logic.usecase.task


import org.example.logic.*
import org.example.logic.entities.Task
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend fun getAllTasks(): List<Task> {
        return taskRepository.getAllTasks().takeIf { it.isNotEmpty() }
            ?: throw TasksNotFoundException()
    }

    suspend fun getTaskByName(taskName: String): Task =
        taskRepository.getAllTasks().find { it.name.equals(taskName, ignoreCase = true) }
            ?: throw TaskNotFoundException()

    suspend fun getTaskIdByName(taskName: String): UUID =
        getTaskByName(taskName).id

    suspend fun addTask(task: Task, projectId: UUID): Boolean {
        val isDuplicate =
            taskRepository.getTasksInProject(projectId = projectId).any {
                it.name.equals(task.name, ignoreCase = true)
            }
        if (isDuplicate) throw DuplicateTaskNameException()

        return taskRepository.addTask(task).also { isAdded ->
            if (!isAdded) throw TaskNotAddedException()
        }
    }

    suspend fun updateTask(updatedTask: Task): Boolean =
        taskRepository.updateTask(updatedTask).also { isUpdated ->
            if (!isUpdated) throw TaskNotEditException()
        }

    suspend fun deleteTaskByName(taskName: String): Boolean =
        taskRepository.deleteTask(getTaskIdByName(taskName)).also { isDeleted ->
            if (!isDeleted) throw TaskNotDeletedException()
        }
}
