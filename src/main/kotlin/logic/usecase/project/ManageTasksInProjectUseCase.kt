package org.example.logic.usecase.project

import logic.model.entities.Task
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.UUID

class ManageTasksInProjectUseCase(
    private val taskUseCase: ManageTasksUseCase,
    private val taskRepository: TaskRepository

) {

    fun getTasksAssignedToProject(projectId: UUID): Result<List<Task>> {
        return taskRepository.getTasksInProject(projectId = projectId).fold(
            onSuccess = { tasksIds ->
                tasksIds.mapNotNull {
                    taskUseCase.getTaskById(it.id).fold(
                        onSuccess = { task -> task },
                        onFailure = { null }
                    )
                }.let {
                    Result.success(it)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )

    }

    fun addTaskToProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.addTaskInProject(projectId = projectId, taskId = taskId)
    }

    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.getTasksInProject(projectId = projectId).fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }?.let { task ->

                    taskRepository.deleteTaskFromProject(projectId = projectId, taskId = taskId)


                } ?: Result.failure(Exception("Couldn't delete"))

            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

}