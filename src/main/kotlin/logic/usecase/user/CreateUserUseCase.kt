package org.example.logic.usecase.user

import logic.models.entities.User
import logic.models.exceptions.UserExceptions
import logic.usecase.validation.ValidateUserDataUseCase
import org.example.logic.repository.UserRepository
import org.example.logic.utils.hashToMd5

class CreateUserUseCase(
    private val userRepository: UserRepository,
    private val validateUserDataUseCase: ValidateUserDataUseCase
) {

    suspend fun createUser(username: String, password: String): Boolean {
        validateUserDataUseCase.validateUserName(username)
        validateUserDataUseCase.validatePassword(password)

        val users = userRepository.getAllUsers()
        val user = handleSuccess(username = username, password = password, users = users)
        return userRepository.addUser(user)
    }


    private fun handleSuccess(
        username: String,
        password: String,
        users: List<User>
    ): User {
        checkUserExists(users, username)
        return User(username = username, hashedPassword = hashToMd5(password))
    }

    fun checkUserExists(users: List<User>, username: String) {
        users.forEach {
            if (it.username == username) throw UserExceptions.UserExistException()
        }
    }

}



