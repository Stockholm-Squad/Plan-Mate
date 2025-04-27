package org.example.data.datasources.authentication

import logic.model.entities.User

interface AuthenticationDataSource {
    fun getUserByUserName(userName: String): Result<User>
    fun getUserById(id: String): Result<User>
    fun addUser(user: User): Result<Boolean>
    fun getAllUsers(): Result<List<User>>
    fun editUser(user: User): Result<Boolean>
    fun deleteUser(id: String): Result<Boolean>
    fun authenticateUser(userName: String, hashedPassword: String): Result<User>
}