package org.example.logic.usecase.task

import logic.model.entities.Task
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.TaskRepository
import kotlin.Result
import kotlin.String


class ManageTasksUseCase(private val taskRepository: TaskRepository) {

    fun getAllTasks(): Result<List<Task>> =
        taskRepository.getAllTasks().fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksFound()) }
        )

    fun getTaskById(taskId: String): Result<Task> =
        taskRepository.getAllTasks().fold(
            onSuccess = { tasks ->
                tasks.find { it.id == taskId }
                    ?.let { Result.success(it) }
                    ?: Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException())
            },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException()) }
        )

    fun createTask(task: Task): Result<Boolean> =
         taskRepository.createTask(task).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksCreated()) }
        )

    fun editTask(updatedTask: Task): Result<Boolean> =
        taskRepository.editTask(updatedTask).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.DidNotUpdateProject()) }
        )

    fun deleteTask(taskId: String): Result<Boolean> =
        getTaskById(taskId).fold(
            onSuccess = {
                taskRepository.deleteTask(taskId).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksDeleted()) }
                )
            },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.TaskNotFoundException()) }
        )
}




//
//    fun getAllTasksByProjectId(projectId: String): Result<List<TaskInProject>> =
//        taskRepository.getAllTasksInProjects().fold(
//            onSuccess = { links ->
//                val filtered = links.filter { it.projectId == projectId }
//                if (filtered.isEmpty()) Result.failure(PlanMateExceptions.LogicException.NoTasksFound())
//                else Result.success(filtered)
//            },
//            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksFound()) }
//        )
//
//    fun getAllStatesByProjectId(projectId: String): Result<List<Pair<String, String>>> =
//        getAllTasksByProjectId(projectId).fold(
//            onSuccess = { tasks ->
//                val states = tasks.map { Pair(it.taskId, it.stateId) }
//                Result.success(states)
//            },
//            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoTasksFound()) }
//        )
//
//    fun promptAndValidateState(projectId: String): Result<String?> {
//        return getAllStatesByProjectId(projectId).fold(
//            onSuccess = { states ->
//                states
//                    .map { it.second }
//                    .firstOrNull()
//                    .let { state ->
//                        state?.let { Result.success(it) }
//                            ?: Result.failure(PlanMateExceptions.LogicException.NoStatesFound())
//                    }
//            },
//            onFailure = {
//                Result.failure(PlanMateExceptions.LogicException.NoStatesFound())
//            }
//        )
//    }
//
//    fun getAllProjects(): Result<List<TaskInProject>> =
//        taskRepository.getAllTasksInProjects().fold(
//            onSuccess = { Result.success(it.distinctBy { link -> link.projectId }) },
//            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoProjectAdded()) }
//        )
//
//    fun getProjectIdByName(projectName: String): Result<String> =
//        taskRepository.getAllTasksInProjects().fold(
//            onSuccess = { projects ->
//                projects.find { it.projectId == projectName }?.projectId
//                    ?.let { Result.success(it) }
//                    ?: Result.failure(PlanMateExceptions.LogicException.ProjectNotFoundException())
//            },
//            onFailure = { Result.failure(PlanMateExceptions.LogicException.ProjectNotFoundException()) }
//        )
