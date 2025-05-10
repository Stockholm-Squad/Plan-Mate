package org.example.data.source.remote.impl

import data.dto.UserAssignedToProjectDto
import org.example.data.source.UserAssignedToProjectDataSource
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

class UserAssignedToProjectMongoDataSource(
    private val userAssignedToProjectCollection: CoroutineCollection<UserAssignedToProjectDto>
) : UserAssignedToProjectDataSource {

    override suspend fun addUserToProject(projectId: String, userName: String): Boolean {
        val document = UserAssignedToProjectDto(
            userName = userName,
            projectId = projectId
        )

        val result = userAssignedToProjectCollection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean {
        val filter = and(
            UserAssignedToProjectDto::userName eq userName,
            UserAssignedToProjectDto::projectId eq projectId
        )

        val result = userAssignedToProjectCollection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> {
        val filter = UserAssignedToProjectDto::projectId eq projectId
        return userAssignedToProjectCollection.find(filter).toList()
    }

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> {
        val filter = UserAssignedToProjectDto::userName eq userName
        return userAssignedToProjectCollection.find(filter).toList()
    }
}
