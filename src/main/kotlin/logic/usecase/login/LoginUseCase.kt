package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.IncorrectPasswordException
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.HashingService

class LoginUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase,
    private val hashingService: HashingService,
) {
    private var currentUser: User? = null

    suspend fun loginUser(username: String, password: String): User =
        validateUserDataUseCase.isValidUserName(username.trim()).takeIf { isValidUserName ->
            isValidUserName
        }?.let {
            validateUserDataUseCase.isValidPassword(password.trim()).takeIf { isValidPassword ->
                isValidPassword
            }?.let {
                getUserIfExist(username.trim(), password.trim())
            } ?: throw InvalidPasswordException()
        } ?: throw InvalidUserNameException()


    private suspend fun getUserIfExist(userName: String, password: String): User =
        getUser(userName)?.let { user ->
            if (user.username != userName) throw UserDoesNotExistException()
            if (isCorrectPassword(password, user.hashedPassword)) {
                currentUser = user
                user
            } else throw IncorrectPasswordException()
        } ?: throw UserDoesNotExistException()

    private fun isCorrectPassword(passwordToBeLoggedIn: String, hashedUserPassword: String): Boolean =
        hashingService.verify(passwordToBeLoggedIn, hashedUserPassword)

    private suspend fun getUser(userName: String): User? =
        userName.trim().let {
            userRepository.getAllUsers().find { it.username == userName }
        }

    suspend fun isUserExist(userName: String): Boolean =
        getUser(userName) != null

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? = currentUser

}