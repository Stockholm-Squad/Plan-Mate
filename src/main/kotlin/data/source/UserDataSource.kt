package org.example.data.source

import org.example.data.models.UserModel

interface UserDataSource {
    suspend fun addUser(user: UserModel): Boolean
    suspend fun getAllUsers(): List<UserModel>
    suspend fun getUsersByProjectId(projectId: String): List<UserModel>
    suspend fun isUserExist(username: String): Boolean
    suspend fun getUserById(userId: String): UserModel?
    suspend fun editUser(user: UserModel): Boolean
    suspend fun deleteUser(user: UserModel): Boolean
}