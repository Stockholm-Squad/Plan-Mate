package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.IncorrectPasswordException
import org.example.logic.InvalidPasswordException
import org.example.logic.InvalidUserNameException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.HashingService

class LoginUseCase(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
    private val validateUserDataUseCase: ValidateUserDataUseCase,
) {
    suspend fun loginUser(username: String, password: String) =
        validateUserDataUseCase.isValidUserName(username.trim()).takeIf { isValidUserName ->
            isValidUserName
        }?.let {
            validateUserDataUseCase.isValidPassword(password.trim()).takeIf { isValidPassword ->
                isValidPassword
            }?.let {
                val user = userRepository.getUserByUsername(username.trim())
                if (!hashingService.verify(password.trim(), user.hashedPassword)) {
                    throw IncorrectPasswordException()
                }
                userRepository.loginUser(user)
            } ?: throw InvalidPasswordException()
        } ?: throw InvalidUserNameException()


    suspend fun isUserExist(userName: String): Boolean =
        userRepository.getUserByUsername(userName).let { true }

    fun logout() {
        userRepository.logoutUser()
    }

    fun getCurrentUser(): User? = userRepository.getCurrentUser()

}