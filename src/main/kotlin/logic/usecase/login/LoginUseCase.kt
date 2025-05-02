package logic.usecase.login


import logic.model.entities.User
import org.example.logic.model.exceptions.*
import org.example.logic.repository.UserRepository
import org.example.utils.hashToMd5

class LoginUseCase(
    private val userRepository: UserRepository
) {
    fun loginUser(username: String, password: String): Result<User> {
        return runCatching {
            validateUserName(username)
            validatePassword(password)
            userRepository.getAllUsers().fold(
                onSuccess = { handleSuccess(username = username, password = password, users = it) },
                onFailure = { handleFailure(it as UserDoesNotExist) })
        }.fold(
            onSuccess = { it },
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

    private fun handleSuccess(username: String, password: String, users: List<User>): Result<User> {
        return runCatching {
            val user = checkUserExists(users, username)
            checkPassword(password, user)
        }.fold(onSuccess = { Result.success(it) }, onFailure = { Result.failure(it as Exception) })

    }

    private fun handleFailure(exceptions: Exception): Result<User> {
        return Result.failure(exceptions)
    }

    private fun checkUserExists(users: List<User>, username: String): User {
        return users.find { it.username == username }
            ?: throw UserDoesNotExist()
    }

    private fun checkPassword(password: String, user: User): User {
        if (hashToMd5(password) == user.hashedPassword) {
            return user
        } else {
            throw IncorrectPassword()
        }
    }


}