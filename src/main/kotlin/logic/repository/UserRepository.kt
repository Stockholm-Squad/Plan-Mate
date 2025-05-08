package org.example.logic.repository

import logic.models.entities.User
import java.util.*

interface UserRepository {
    suspend fun addUser(user: User): Boolean
    suspend fun getAllUsers(): List<User>
    suspend fun getUsersByProjectId(projectId: UUID): List<User>
    suspend fun addUserToProject(projectId: UUID, userName: String): Boolean
    suspend fun deleteUserFromProject(projectId: UUID, userName: String): Boolean
    suspend fun addUserToTask(mateName: String, taskId: UUID): Boolean
    suspend fun deleteUserFromTask(mateName: String, taskId: UUID): Boolean
}
