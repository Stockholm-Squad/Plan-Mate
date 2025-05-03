package org.example.logic.usecase.task

import data.models.MateTaskAssignment
import org.example.logic.model.exceptions.NoTaskAssignmentFound
import org.example.logic.repository.TaskRepository
// TODO Check The Usage of this
class GetTasksAssignedToUserUseCase(
    private val taskRepository: TaskRepository
) {

    fun getAllMateTaskAssignment(userName: String): Result<List<MateTaskAssignment>> =
        taskRepository.getAllTasksByUserName(userName).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(NoTaskAssignmentFound()) }
        )
}