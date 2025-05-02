package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.model.exceptions.*
import org.example.logic.repository.UserRepository
import org.example.utils.hashToMd5

class CreateUserUseCase(private val userRepository: UserRepository) {

    fun createUser(username: String, password: String): Result<Boolean> {
        return runCatching {
            validateUserName(username)
            validatePassword(password)

            userRepository.getAllUsers().fold(
                onSuccess = {
                    handleSuccess(username = username, password = password, users = it).fold(
                        onSuccess = { userRepository.createUser(it) },
                        onFailure = { handleFailure(it) })
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
            onSuccess = { Result.success(User(username, hashedPassword = hashToMd5(password))) },
            onFailure = { Result.failure(it) })

    }

    private fun handleFailure(exceptions: Exception): Result<Boolean> {
        return Result.failure(exceptions)
    }

    fun checkUserExists(users: List<User>, username: String) {
        users.find { it.username == username }.let { throw UserExist() }

    }

}



