package org.example.logic.usecase.task

import data.models.MateTaskAssignment
import org.example.logic.model.exceptions.NoTaskAssignmentFound
import org.example.logic.repository.TaskRepository

class GetTasksAssignedToUserUseCase(
    private val taskRepository: TaskRepository
) {

    fun getAllMateTaskAssignment(userName: String): Result<List<MateTaskAssignment>> =
        taskRepository.getAllMateTaskAssignment(userName).fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(NoTaskAssignmentFound()) }
        )
}