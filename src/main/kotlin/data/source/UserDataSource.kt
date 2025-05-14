package org.example.data.source

import data.dto.UserAssignedToProjectDto
import data.dto.UserDto

interface UserDataSource {
    suspend fun addUser(user: UserDto): Boolean
    suspend fun getAllUsers(): List<UserDto>
    suspend fun getUsersByProjectId(projectId: String): List<UserDto>
    suspend fun isUserExist(username: String): Boolean
    suspend fun getUserById(userId: String): UserDto?
    suspend fun updateUser(user: UserDto): Boolean
    suspend fun deleteUser(user: UserDto): Boolean
    suspend fun deleteUserFromProject(projectId: String, username: String): Boolean
    suspend fun addUserToProject(projectId: String, username: String): Boolean
    suspend fun addUserToTask(username: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(username: String, taskId: String): Boolean
    suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto>

}