package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
import org.example.data.utils.tryToExecute
import org.example.logic.IncorrectPasswordException
import org.example.logic.UserDoesNotExistException
import org.example.logic.UsersDataAreEmptyException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.hashToMd5

class LoginUseCase(
    private val userRepository: UserRepository, private val validateUserDataUseCase: ValidateUserDataUseCase
) {
    private var currentUser: User? = null

    suspend fun loginUser(username: String, password: String): User {
        return tryToExecute({
            validateUserDataUseCase.validateUserName(username)
            validateUserDataUseCase.validatePassword(password)
            handleSuccess(
                username = username, password = password, users = userRepository.getAllUsers()
            )
        }, onSuccess = {
            currentUser = it
            it
        }, onFailure = { throw UsersDataAreEmptyException() })
    }

    private suspend fun handleSuccess(username: String, password: String, users: List<User>): User {
        return tryToExecute(
            {
                val user = checkUserExists(users, username)
                checkPassword(password, user)
            },
            onSuccess = { it },
            onFailure = { throw UsersDataAreEmptyException() },
        )
    }

    private suspend fun checkUserExists(users: List<User>, username: String): User {
        return tryToExecute({
            users.find { it.username == username }!!
        }, onSuccess = { it }, onFailure = { throw UserDoesNotExistException() })
    }

    private suspend fun checkPassword(password: String, user: User): User {
        return tryToExecute({
            if (hashToMd5(password) == user.hashedPassword) {
                user
            } else {
                throw IncorrectPasswordException()
            }
        }, onSuccess = { it }, onFailure = { throw IncorrectPasswordException() })
    }

    suspend fun isUserExists(userName: String): Boolean {
        return tryToExecute(
            { userRepository.getAllUsers().any { it.username == userName } },
            onSuccess = { it },
            onFailure = { false })
    }

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}