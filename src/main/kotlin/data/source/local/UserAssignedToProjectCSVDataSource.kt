package org.example.data.source.local

import data.dto.UserAssignedToProjectDto
import org.example.data.source.UserAssignedToProjectDataSource

class UserAssignedToProjectCSVDataSource : UserAssignedToProjectDataSource {
    override suspend fun addUserToProject(projectId: String, userName: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> {
        TODO("Not yet implemented")
    }
}