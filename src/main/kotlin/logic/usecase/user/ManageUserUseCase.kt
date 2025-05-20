package org.example.logic.usecase.user

import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.UserExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.project.GetProjectsUseCase
import org.example.logic.utils.HashingService
import java.util.*

class ManageUserUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase,
    private val loginUseCase: LoginUseCase,
    private val hashingService: HashingService,
    private val getProjectsUseCase: GetProjectsUseCase,
) {

    suspend fun addUser(username: String, password: String): Boolean {
        if (!validateUserDataUseCase.isValidUserName(username.trim())) throw InvalidUserNameException()
        if (!validateUserDataUseCase.isValidPassword(password.trim())) throw InvalidPasswordException()

        val user = createUserIfNotExist(username = username.trim(), password = password.trim())
        return userRepository.addUser(user)
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

    suspend fun getUsersByProjectId(projectId: UUID): List<User> {
        return userRepository.getUsersByProjectId(projectId = projectId)
    }

    private suspend fun createUserIfNotExist(username: String, password: String): User {
        loginUseCase.isUserExist(username).also { exist ->
            if (!exist) {
                return User(username = username, hashedPassword = hashingService.hash(password))
            } else {
                throw UserExistException()
            }
        }
    }
}