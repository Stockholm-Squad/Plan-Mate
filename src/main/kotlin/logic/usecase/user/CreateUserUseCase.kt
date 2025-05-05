package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.model.exceptions.UserExistException
import org.example.logic.model.exceptions.UsersDataAreEmptyException
import org.example.logic.repository.UserRepository
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.utils.hashToMd5

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) {

    fun createUser(username: String, password: String): Result<Boolean> {
            try {
                validateUserDataUseCase.validateUserName(username)
                validateUserDataUseCase.validatePassword(password)

                return userRepository.getAllUsers().fold(
                    onSuccess = {
                        handleSuccess(username = username, password = password, users = it).fold(
                            onSuccess = { user -> userRepository.addUser(user) },
                            onFailure = { throwable -> handleFailure(throwable) })
                    },
                    onFailure = { handleFailure(it as UsersDataAreEmptyException) })
            } catch (e: Exception) {
                return Result.failure(e)
            }
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

    private fun handleFailure(throwable: Throwable): Result<Boolean> {
        return Result.failure(throwable)
    }

    fun checkUserExists(users: List<User>, username: String) {
        users.forEach {
            if (it.username == username) throw UserExistException()
        }

    }

}



