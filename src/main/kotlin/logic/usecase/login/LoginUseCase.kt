package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.IncorrectPasswordException
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.hashToMd5

class LoginUseCase(
    private val userRepository: UserRepository, private val validateUserDataUseCase: ValidateUserDataUseCase,
) {
    private var currentUser: User? = null

    suspend fun loginUser(username: String, password: String): User {
        if (!validateUserDataUseCase.isValidUserName(username.trim())) throw InvalidUserNameException()
        if (!validateUserDataUseCase.isValidPassword(password.trim())) throw InvalidPasswordException()
        return getUserIfExist(username.trim(), password.trim())
    }

    private suspend fun getUserIfExist(userName: String, password: String): User {
        val user = getUser(userName) ?: throw UserDoesNotExistException()
        if (user.username != userName) throw UserDoesNotExistException()
        return if (isCorrectPassword(password, user.hashedPassword)) {
            currentUser = user
            user
        } else throw IncorrectPasswordException()
    }

    private fun isCorrectPassword(passwordToBeLoggedIn: String, userPassword: String): Boolean {
        return hashToMd5(passwordToBeLoggedIn) == userPassword
    }

    private suspend fun getUser(userName: String): User? {
        userName.trim()
        return userRepository.getAllUsers().find { it.username == userName }
    }

    suspend fun isUserExist(userName: String): Boolean {
        return getUser(userName) != null
    }

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}