package org.example.data.repo

import logic.model.entities.User
import org.example.data.datasources.authentication.AuthenticationDataSource
import org.example.logic.repository.AuthenticationRepository

class AuthenticationRepositoryImp(
    private val authenticationDataSource: AuthenticationDataSource
) : AuthenticationRepository {

    override fun getUserByUserName(userName: String): Result<User> {
        TODO("Not yet implemented")
    }

    override fun getUserById(id: String): Result<User> {
        TODO("Not yet implemented")
    }

    override fun addUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): Result<List<User>> {
        TODO("Not yet implemented")
    }

    override fun editUser(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteUser(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun authenticateUser(
        userName: String,
        password: String
    ): Result<User> {
        TODO("Not yet implemented")
    }
}