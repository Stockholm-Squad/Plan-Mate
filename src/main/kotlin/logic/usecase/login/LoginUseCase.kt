package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
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

    suspend fun loginUser(username: String, password: String): User = try {
        validateUserDataUseCase.validateUserName(username)
        validateUserDataUseCase.validatePassword(password)
        handleSuccess(
            username = username, password = password, users = userRepository.getAllUsers()
        )
    } catch (ex: Exception) {
        throw UsersDataAreEmptyException()
    }


    private suspend fun handleSuccess(username: String, password: String, users: List<User>): User = try {
        val user = checkUserExists(users, username)
        checkPassword(password, user)
    } catch (ex: Exception) {
        throw UsersDataAreEmptyException()
    }


    private suspend fun checkUserExists(users: List<User>, username: String): User = try {
        users.find { user -> user.username == username }!!
    } catch (ex: Exception) {
        throw UserDoesNotExistException()
    }


    private suspend fun checkPassword(password: String, user: User): User = try {
        if (hashToMd5(password) == user.hashedPassword) {
            user
        } else {
            throw IncorrectPasswordException()
        }
    } catch (ex: Exception) {
        throw IncorrectPasswordException()
    }

    suspend fun isUserExists(userName: String): Boolean = try {
        userRepository.getAllUsers().any { it.username == userName }
    } catch (ex: Exception) {
        false
    }

    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? = currentUser

}