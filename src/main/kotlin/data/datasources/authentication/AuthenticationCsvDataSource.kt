package org.example.data.datasources.authentication

import logic.model.entities.User

class AuthenticationCsvDataSource : AuthenticationDataSource {
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
        hashedPassword: String
    ): Result<User> {
        TODO("Not yet implemented")
    }
}