package org.example.data.datasources

import org.example.logic.entities.User

interface AuthenticationDataSource {
    fun getUserByUserName(userName: String): Result<User>
    fun getUserById(id: String): Result<User>
    fun addUser(user: User): Result<Boolean>
    fun getAllUsers(): Result<List<User>>
    fun editUser(user: User): Result<Boolean>
    fun deleteUser(id: String): Result<Boolean>
    fun authenticateUser(userName: String, password: String): Result<User> //TODO: password or hashedPassword
}