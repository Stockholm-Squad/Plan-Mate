package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.repository.AuthenticationRepository
import java.security.MessageDigest

class AddUserUseCase(private val authenticationRepository: AuthenticationRepository) {

    fun addUser(newUser: User): Result<Boolean> {
        val newUserWithHashedPassword = User(newUser.username, md5Hash(newUser.hashedPassword))
        return authenticationRepository.getAllUsers()
            .mapCatching { existingUsers ->
                // Validate no duplicate username
                if (existingUsers.any { it.username == newUser.username }) {
                    throw IllegalArgumentException("User ${newUser.username} already exists")
                }

                // Append new user to file
                authenticationRepository.addUser(newUserWithHashedPassword).getOrThrow()
            }
    }

    private fun md5Hash(password: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digested = md.digest(password.toByteArray())
            digested.fold("") { str, byte -> str + "%02x".format(byte) }
        } catch (e: Exception) {
            throw RuntimeException("Failed to hash password", e)
        }
    }
}

