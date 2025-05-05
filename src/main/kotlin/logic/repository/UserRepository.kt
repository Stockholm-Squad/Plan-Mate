package org.example.logic.repository

import logic.model.entities.User
import java.util.*

interface UserRepository {
    fun addUser(user: User): Result<Boolean>
    fun getAllUsers(): Result<List<User>>
    fun getUsersByProjectId(projectId: UUID): Result<List<User>>
    fun addUserToProject(projectId: UUID, userName: String): Result<Boolean>
    fun deleteUserFromProject(projectId: UUID, userName: String): Result<Boolean>
    fun addUserToTask(mateName: String, taskId: UUID): Result<Boolean>
    fun deleteUserFromTask(mateName: String, taskId: UUID): Result<Boolean>
}
