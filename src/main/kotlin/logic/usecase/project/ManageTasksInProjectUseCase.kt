package org.example.logic.usecase.project

import logic.model.entities.Task
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.task.ManageTasksUseCase

class ManageTasksInProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val taskUseCase: ManageTasksUseCase
) {

    fun getTasksAssignedToProject(projectId: String): Result<List<Task>> {
        return projectRepository.getTasksInProject(projectId = projectId).fold(
            onSuccess = { tasksIds ->
                tasksIds.mapNotNull {
                    taskUseCase.getTaskById(it).fold(
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

    fun addTaskAssignedToProject(projectId: String, taskId: String): Result<Boolean> {
        return projectRepository.addTaskInProject(projectId = projectId, taskId = taskId)
    }

    fun deleteTaskAssignedToProject(projectId: String, taskId: String): Result<Boolean> {
        return projectRepository.getTasksInProject(projectId = projectId).fold(
            onSuccess = { tasksIds ->
                when (tasksIds.contains(taskId)) {
                    true -> projectRepository.deleteTaskFromProject(projectId = projectId, taskId = taskId)
                    false -> Result.success(false)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

}