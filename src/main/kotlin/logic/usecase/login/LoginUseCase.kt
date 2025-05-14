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
) {
    suspend fun loginUser(username: String, password: String): User =
        validateUserDataUseCase.isValidUserName(username.trim()).takeIf { isValidUserName ->
            isValidUserName
        }?.let {
            validateUserDataUseCase.isValidPassword(password.trim()).takeIf { isValidPassword ->
                isValidPassword
            }?.let {
                userRepository.loginUser(username, password)
            } ?: throw InvalidPasswordException()
        } ?: throw InvalidUserNameException()


    suspend fun isUserExist(userName: String): Boolean =
        userRepository.getUserByUsername(userName).let { true }

    fun logout() {
        userRepository.logoutUser()
    }

    fun getCurrentUser(): User? = userRepository.getCurrentUser()

}