package org.example.logic.repository

import logic.model.entities.User

interface UserRepository {
    fun getUserByUserName(userName: String): Result<User>
    fun addUser(user: User): Result<Boolean>
    fun getAllUsers(): Result<List<User>>
}