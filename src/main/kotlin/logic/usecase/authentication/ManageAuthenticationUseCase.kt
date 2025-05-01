package org.example.logic.usecase.authentication

import org.example.logic.repository.AuthenticationRepository

class ManageAuthenticationUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    fun isUserExists(username: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

}