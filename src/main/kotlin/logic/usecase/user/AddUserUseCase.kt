package org.example.logic.usecase.user

import logic.usecase.login.LoginUseCase
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.hashToMd5

class AddUserUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase,
    private val loginUseCase: LoginUseCase,
) {

    suspend fun addUser(username: String, password: String): Boolean {
        if (!validateUserDataUseCase.isValidUserName(username.trim())) throw InvalidUserNameException()
        if (!validateUserDataUseCase.isValidPassword(password.trim())) throw InvalidPasswordException()

        val user = createUserIfNotExist(username = username.trim(), password = password.trim())
        return userRepository.addUser(user)
    }

    private suspend fun createUserIfNotExist(
        username: String,
        password: String,
    ): User {
        loginUseCase.isUserExist(username).also { exist ->
            if (!exist) {
                return User(username = username, hashedPassword = hashToMd5(password))
            } else {
                throw UserExistException()
            }
        }
    }
}