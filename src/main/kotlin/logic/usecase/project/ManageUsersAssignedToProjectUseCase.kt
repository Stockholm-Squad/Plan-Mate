package org.example.logic.usecase.project

import logic.usecase.login.LoginUseCase
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
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
        return loginUseCase.isUserExists(userName).let { success ->
            if (success)
                userRepository.addUserToProject(projectId = projectId, userName = userName)
            else
                throw UserDoesNotExistException()
        }
    }


    suspend fun deleteUserFromProject(projectName: String, username: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let { project ->
            loginUseCase.isUserExists(username).let { success ->
                if (success)
                    userRepository.deleteUserFromProject(projectId = project.id, userName = username)
                else
                    throw UserDoesNotExistException()
            }
        }
    }
}