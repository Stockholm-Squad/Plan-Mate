package org.example.logic.usecase.project

import logic.model.entities.Task
import org.example.logic.repository.TaskRepository
import org.example.logic.usecase.task.ManageTasksUseCase
import java.util.UUID

class ManageTasksInProjectUseCase(
    private val taskUseCase: ManageTasksUseCase,
    private val projectUseCase: ManageProjectUseCase,
    private val taskRepository: TaskRepository

) {

    fun getTasksInProjectByName(projectName: String): Result<List<Task>> {
        return projectUseCase.getProjectByName(projectName).fold(
            onSuccess = { project ->
                taskRepository.getTasksInProject(project.id).fold(
                    onSuccess = { taskRefs ->
                        taskRefs.mapNotNull { taskRef ->
                            getTaskByName(taskRef.name).getOrNull()
                        }.let { Result.success(it) }
                    },
                    onFailure = { throwable -> Result.failure(throwable) }
                )
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }



    fun getTasksInProject(projectId: UUID): Result<List<Task>> {
        return taskRepository.getTasksInProject(projectId)
    }

    fun getTasksInProjectByName(ProjectName: String): Result<List<Task>> {
        return projectUseCase.getProjectByName(projectUseCase)
    }

    fun addTaskToProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.addTaskInProject(projectId = projectId, taskId = taskId)
    }

    fun deleteTaskFromProject(projectId: UUID, taskId: UUID): Result<Boolean> {
        return taskRepository.deleteTaskFromProject(projectId, taskId)
    }

    fun getAllTasksByUserName(userName: String): Result<List<Task>> {
        return taskRepository.getAllTasksByUserName(userName)
    }

}