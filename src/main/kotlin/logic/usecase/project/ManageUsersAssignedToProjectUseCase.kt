package org.example.logic.usecase.project

import logic.models.entities.User
import logic.usecase.login.LoginUseCase
import org.example.logic.repository.UserRepository
import java.util.*

class ManageUsersAssignedToProjectUseCase(
    private val userRepository: UserRepository,
    private val getProjectsUseCase: GetProjectsUseCase,
    private val loginUseCase: LoginUseCase,

    ) {

    suspend fun getUsersByProjectId(projectId: UUID): List<User> {
        return userRepository.getUsersByProjectId(projectId = projectId)
    }


    suspend fun addUserToProject(projectId: UUID, userName: String): Boolean {
        return userRepository.addUserToProject(projectId = projectId, userName = userName)
    }


    suspend fun deleteUserFromProject(projectName: String, username: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let { project ->
            loginUseCase.isUserExists(username).let { success ->
                userRepository.deleteUserFromProject(projectId = project.id, userName = username)
            }
        }
    }
}