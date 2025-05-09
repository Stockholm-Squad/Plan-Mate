package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProjectModel

interface UserAssignedToProjectDataSource {
    suspend fun addUserToProject(projectId: String, userName: String): Boolean
    suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean
    suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectModel>
    suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectModel>
}