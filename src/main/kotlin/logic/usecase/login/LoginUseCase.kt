package logic.usecase.login


import logic.usecase.validation.ValidateUserDataUseCase
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.IncorrectPasswordException
import org.example.logic.UserDoesNotExistException
import org.example.logic.UsersDataAreEmptyException
import org.example.logic.entities.User
import org.example.logic.repository.UserRepository
import org.example.logic.utils.hashToMd5

class LoginUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) {

    suspend fun loginUser(username: String, password: String): User =
        executeSafelyWithContext(
            onSuccess = {
                validateUserDataUseCase.validateUserName(username)
                validateUserDataUseCase.validatePassword(password)
                userRepository.getAllUsers().let {
                    handleSuccess(
                        username = username,
                        password = password,
                        users = it
                    )
                }

            }, onFailure = {
                throw UsersDataAreEmptyException()
            }
        )

    private suspend fun handleSuccess(username: String, password: String, users: List<User>): User =
        executeSafelyWithContext(
            onSuccess = {
                val user = checkUserExists(users, username)
                checkPassword(password, user)
            }, onFailure = {
                throw UsersDataAreEmptyException()
            }
        )

    private suspend fun checkUserExists(users: List<User>, username: String): User =
        executeSafelyWithContext(
            onSuccess = {
                users.find { it.username == username }!!
            }, onFailure = {
                throw UserDoesNotExistException()
            }
        )

    private suspend fun checkPassword(password: String, user: User): User =
        executeSafelyWithContext(
            onSuccess = {
                if (hashToMd5(password) == user.hashedPassword) {
                    user
                } else {
                    throw IncorrectPasswordException()
                }
            },
            onFailure = {
                throw IncorrectPasswordException()
            }
        )

    suspend fun isUserExists(userName: String): Boolean =
        executeSafelyWithContext(
            onSuccess = {
                userRepository.getAllUsers().any { it.username == userName }
            },
            onFailure = {
                throw UserDoesNotExistException()
            }
        )


}