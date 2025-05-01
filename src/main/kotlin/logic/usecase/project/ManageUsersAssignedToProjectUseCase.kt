package org.example.logic.usecase.project

import logic.model.entities.User
import org.example.logic.repository.AuthenticationRepository
import org.example.logic.repository.ProjectRepository

class ManageUsersAssignedToProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val authRepository: AuthenticationRepository,
) {
    fun getUsersAssignedToProject(projectId: String): Result<List<User>> {
        return projectRepository.getUsersAssignedToProject(projectId = projectId).fold(
            onSuccess = { userNames ->
                userNames.mapNotNull {
                    authRepository.getUserByUserName(it).fold(
                        onSuccess = { user -> user },
                        onFailure = { null }
                    )
                }.let {
                    Result.success(it)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )

    }

    fun addUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return projectRepository.addUserAssignedToProject(projectId = projectId, userName = userName)
    }

    fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return projectRepository.deleteUserAssignedToProject(projectId = projectId, userName = userName)
    }

}