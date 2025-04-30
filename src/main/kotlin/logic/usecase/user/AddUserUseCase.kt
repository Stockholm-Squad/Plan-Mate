package org.example.logic.usecase.user

import logic.model.entities.User
import org.example.logic.repository.AuthenticationRepository

class AddUserUseCase(val authenticationRepository: AuthenticationRepository) {

        fun addUser(newUser: User): Result<Boolean> {
            return authenticationRepository.getAllUsers()
                .mapCatching { existingUsers ->
                    // Validate no duplicate username
                    if (existingUsers.any { it.username == newUser.username }) {
                        throw IllegalArgumentException("User ${newUser.username} already exists")
                    }

                    // Append new user to file
                    authenticationRepository.addUser(newUser).getOrThrow()
                }
        }

}