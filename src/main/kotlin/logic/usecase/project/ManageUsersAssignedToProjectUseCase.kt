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
        return loginUseCase.isUserExist(userName).let { isUserExist ->
            if (isUserExist)
                userRepository.addUserToProject(projectId = projectId, username = userName)
            else
                throw UserDoesNotExistException()
        }
    }


    suspend fun deleteUserFromProject(projectName: String, username: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let { project ->
            loginUseCase.isUserExist(username).let { isUserExist ->
                if (isUserExist)
                    userRepository.deleteUserFromProject(projectId = project.id, username = username)
                else
                    throw UserDoesNotExistException()
            }
        }
    }
}