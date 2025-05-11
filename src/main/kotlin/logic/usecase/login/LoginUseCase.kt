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

    suspend fun loginUser(username: String, password: String): User = tryToExecute({
        validateUserDataUseCase.validateUserName(username)
        validateUserDataUseCase.validatePassword(password)
        handleSuccess(
            username = username, password = password, users = userRepository.getAllUsers()
        )
    }, onSuccess = { user ->
        currentUser = user
        user
    }, onFailure = { throw UsersDataAreEmptyException() })


    private suspend fun handleSuccess(username: String, password: String, users: List<User>): User = tryToExecute(
        {
            val user = checkUserExists(users, username)
            checkPassword(password, user)
        },
        onSuccess = { user -> user },
        onFailure = { throw UsersDataAreEmptyException() },
    )


    private suspend fun checkUserExists(users: List<User>, username: String): User = tryToExecute(
        {
            users.find { user -> user.username == username }!!
        },
        onSuccess = { success -> success },
        onFailure = { throw UserDoesNotExistException() }
    )


    private suspend fun checkPassword(password: String, user: User): User = tryToExecute(
        {
            if (hashToMd5(password) == user.hashedPassword) {
                user
            } else {
                throw IncorrectPasswordException()
            }
        }, onSuccess = { user -> user },
        onFailure = { throw IncorrectPasswordException() }
    )


    suspend fun isUserExists(userName: String): Boolean = tryToExecute(
        { userRepository.getAllUsers().any { it.username == userName } },
        onSuccess = { success -> success },
        onFailure = { false })


    fun logout() {
        currentUser = null
    }

    fun getCurrentUser(): User? = currentUser

}