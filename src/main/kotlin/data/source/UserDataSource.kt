package org.example.data.source

import data.dto.UserDto

interface UserDataSource {
    suspend fun addUser(user: UserDto): Boolean
    suspend fun getAllUsers(): List<UserDto>
    suspend fun getUsersByProjectId(projectId: String): List<UserDto>
    suspend fun isUserExist(username: String): Boolean
    suspend fun getUserById(userId: String): UserDto?
    suspend fun updateUser(user: UserDto): Boolean
    suspend fun deleteUser(user: UserDto): Boolean
}