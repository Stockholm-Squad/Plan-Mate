package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.model.exceptions.*
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.common.ValidateUserDataUseCase
import org.example.utils.hashToMd5

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) {

    fun createUser(username: String, password: String): Result<Boolean> {
        return runCatching {
            validateUserDataUseCase.validateUserName(username)
            validateUserDataUseCase.validatePassword(password)

            userRepository.getAllUsers().fold(
                onSuccess = {
                    handleSuccess(username = username, password = password, users = it).fold(
                        onSuccess = { user ->  userRepository.addUser(user) },
                        onFailure = {exception ->  handleFailure(exception) })
                },
                onFailure = { handleFailure(it as UsersDataAreEmpty) })
        }.fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(exception = it) })
    }


    private fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw InvalidUserName()
    }

    private fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw InvalidPassword()
    }

    private fun handleSuccess(
        username: String,
        password: String,
        users: List<User>
    ): Result<User> {
        return runCatching {
            checkUserExists(users, username)
        }.fold(
            onSuccess = { Result.success(User(username = username, hashedPassword = hashToMd5(password))) },
            onFailure = { Result.failure(it) })

    }

    private fun handleFailure(exceptions: Throwable): Result<Boolean> {
        return Result.failure(exceptions)
    }

    fun checkUserExists(users: List<User>, username: String) {
        users.find { it.username == username }.let { throw UserExist() }

    }

}



