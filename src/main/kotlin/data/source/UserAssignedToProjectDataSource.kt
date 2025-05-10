package org.example.data.source

import data.dto.UserAssignedToProjectDto

interface UserAssignedToProjectDataSource {
    suspend fun addUserToProject(projectId: String, userName: String): Boolean
    suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean
    suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto>
    suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto>
}