package logic.usecase.login


import logic.model.entities.User
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.common.ValidateUserDataUseCase
import org.example.utils.hashToMd5

class LoginUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) {
    fun loginUser(username: String, password: String): Result<User> {
        return try {
            validateUserDataUseCase.validateUserName(username)
            validateUserDataUseCase.validatePassword(password)
            userRepository.getAllUsers().fold(
                onSuccess = { handleSuccess(username = username, password = password, users = it) },
                onFailure = { Result.failure(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun handleSuccess(username: String, password: String, users: List<User>): Result<User> {
        return runCatching {
            val user = checkUserExists(users, username)
            checkPassword(password, user)
        }
    }

    private fun checkUserExists(users: List<User>, username: String): User {
        return users.find { it.username == username }
            ?: throw PlanMateExceptions.LogicException.UserDoesNotExist()
    }

    private fun checkPassword(password: String, user: User): User {
        if (hashToMd5(password) == user.hashedPassword) {
            return user
        } else {
            throw PlanMateExceptions.LogicException.IncorrectPassword()
        }
    }

}