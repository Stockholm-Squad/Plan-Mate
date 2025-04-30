package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.repository.UserRepository
import org.example.utils.hashToMd5
import java.security.MessageDigest
class AddUserUseCase(private val userRepository: UserRepository) {

    fun addUser(newUser: User): Result<Boolean> {
        return userRepository.getAllUsers()
            .fold(
                onSuccess = { onSuccessHandler(newUser, it) },
                onFailure = { error -> Result.failure(error) }
            )
    }

    private fun onSuccessHandler(
        newUser: User,
        existingUsers: List<User>
    ) = try {
        validateNewUser(newUser, existingUsers)
        val hashedUser = createHashedUser(newUser)
        userRepository.addUser(hashedUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun validateNewUser(newUser: User, existingUsers: List<User>) {
        if (existingUsers.any { it.username == newUser.username }) {
            throw IllegalArgumentException("User ${newUser.username} already exists")
        }
    }

     fun createHashedUser(user: User): User {
        return try {
            User(user.username, hashToMd5(user.hashedPassword))
        } catch (e: Exception) {
            throw RuntimeException("Failed to hash password", e)
        }
    }

}
