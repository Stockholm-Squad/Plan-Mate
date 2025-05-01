package org.example.logic.usecase.project

import logic.model.entities.User
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.UserRepository

class ManageUsersAssignedToProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository,
) {
    fun getUsersAssignedToProject(projectId: String): Result<List<User>> {

        return userRepository.getAllUsers().fold(
            onSuccess = { allUsers ->
                projectRepository.getUsersAssignedToProject(projectId = projectId).fold(
                    onSuccess = { userNames ->
                        userNames.mapNotNull { userName ->
                            allUsers.find { it.username == userName }
                        }.let {
                            Result.success(it)
                        }
                    },
                    onFailure = { throwable -> Result.failure(throwable) }
                )
            },
            onFailure = { Result.failure(Throwable()) })
    }

    fun assignUserToProject(projectId: String, userName: String): Result<Boolean> {
        return projectRepository.addUserAssignedToProject(projectId = projectId, userName = userName)
    }

    fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return projectRepository.getUsersAssignedToProject(projectId = projectId).fold(
            onSuccess = { userNames ->
                when (userNames.contains(userName)) {
                    true -> projectRepository.deleteUserAssignedToProject(projectId = projectId, userName = userName)
                    false -> Result.success(false)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

}