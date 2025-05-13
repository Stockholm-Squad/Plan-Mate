package org.example.logic.usecase.task


import org.example.logic.*
import org.example.logic.entities.Task
import org.example.logic.repository.TaskRepository
import java.util.*

class ManageTasksUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend fun getAllTasks(): List<Task> =
        taskRepository.getAllTasks().ifEmpty { throw TasksNotFoundException() }


    suspend fun getTaskByName(taskName: String): Task =
        taskRepository.getAllTasks().find { it.title.equals(taskName, ignoreCase = true) }
            ?: throw TaskNotFoundException()

    suspend fun getTaskIdByName(taskName: String): UUID =
        getTaskByName(taskName).id

    suspend fun addTask(task: Task, projectId: UUID): Boolean =
        if (isTaskDuplicatedInProject(projectId, task)) {
            throw DuplicateTaskNameException()
        } else {
            taskRepository.addTask(task).also { task ->
                if (!task) throw TaskNotAddedException()
            }
        }

    private suspend fun isTaskDuplicatedInProject(projectId: UUID, task: Task): Boolean =
        try {
            taskRepository.getTasksInProject(projectId = projectId).any {
                it.title.equals(task.title, ignoreCase = true)
            }
        } catch (ex: TasksNotFoundException) {
            false
        }


    suspend fun updateTask(updatedTask: Task): Boolean =
        taskRepository.updateTask(updatedTask).also { isUpdated ->
            if (!isUpdated) throw TaskNotUpdatedException()
        }

    suspend fun deleteTaskByName(taskName: String): Boolean =
        taskRepository.deleteTask(getTaskIdByName(taskName)).also { isDeleted ->
            if (!isDeleted) throw TaskNotDeletedException()
        }
}
