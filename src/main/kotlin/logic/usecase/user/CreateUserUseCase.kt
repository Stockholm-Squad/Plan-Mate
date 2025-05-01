package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.model.exceptions.PlanMateExceptions
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
                        onSuccess = { userRepository.addUser(it) },
                        onFailure = { handleFailure(it as PlanMateExceptions) })
                },
                onFailure = { handleFailure(it as PlanMateExceptions.LogicException.UsersIsEmpty) })
        }.fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(exception = it) })
    }


    private fun validateUserName(username: String) {
        if (username.isBlank() || username.length > 20 || username.length < 4 || username.first()
                .isDigit()
        ) throw PlanMateExceptions.LogicException.InvalidUserName()
    }

    private fun validatePassword(password: String) {
        if (password.isBlank() || password.length < 8) throw PlanMateExceptions.LogicException.InvalidPassword()
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
            onFailure = { Result.failure(it as PlanMateExceptions) })

    }

    private fun handleFailure(exceptions: PlanMateExceptions): Result<Boolean> {
        return Result.failure(exceptions)
    }

    fun checkUserExists(users: List<User>, username: String) {
        users.find { it.username == username }.let { throw PlanMateExceptions.LogicException.UserExist() }

    }

}



