package org.example.data.source.remote

import data.dto.UserAssignedToProjectDto
import org.example.data.utils.USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
import org.example.data.source.UserAssignedToProjectDataSource
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserAssignedToProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : UserAssignedToProjectDataSource {

    private val collection = mongoDatabase.getCollection<UserAssignedToProjectDto>(
        USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
    )

    override suspend fun addUserToProject(projectId: String, userName: String): Boolean {
        val document = UserAssignedToProjectDto(
            userName = userName,
            projectId = projectId
        )

        val result = collection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean {
        val filter = and(
            UserAssignedToProjectDto::userName eq userName,
            UserAssignedToProjectDto::projectId eq projectId
        )

        val result = collection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> {
        val filter = UserAssignedToProjectDto::projectId eq projectId
        return collection.find(filter).toList()
    }

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> {
        val filter = UserAssignedToProjectDto::userName eq userName
        return collection.find(filter).toList()
    }
}
