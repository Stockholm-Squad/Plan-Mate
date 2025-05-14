package org.example.logic.repository

import org.example.logic.entities.User
import java.util.*

interface UserRepository {
    suspend fun addUser(user: User): Boolean
    suspend fun getAllUsers(): List<User>
    suspend fun getUsersByProjectId(projectId: UUID): List<User>
    suspend fun addUserToProject(projectId: UUID, username: String): Boolean
    suspend fun deleteUserFromProject(projectId: UUID, username: String): Boolean
    suspend fun addUserToTask(username: String, taskId: UUID): Boolean
    suspend fun deleteUserFromTask(username: String, taskId: UUID): Boolean

    suspend fun getUserByUsername(username: String): User
    suspend fun loginUser(username: String, password: String): User
    fun getCurrentUser(): User?
    fun logoutUser()

}
