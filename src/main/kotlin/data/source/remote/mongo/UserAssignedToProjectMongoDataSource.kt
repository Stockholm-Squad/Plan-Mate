package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.UserAssignedToProjectDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.UserAssignedToProjectDataSource
import org.litote.kmongo.and
import org.litote.kmongo.eq

class UserAssignedToProjectMongoDataSource(
    private val userAssignedToProjectCollection: MongoCollection<UserAssignedToProjectDto>,
) : UserAssignedToProjectDataSource {

    override suspend fun addUserToProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectCollection.insertOne(
            UserAssignedToProjectDto(
                userName = userName,
                projectId = projectId
            )
        ).insertedId != null

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectCollection.deleteOne(
            and(
                UserAssignedToProjectDto::userName eq userName,
                UserAssignedToProjectDto::projectId eq projectId
            )
        ).deletedCount > 0

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectCollection.find(UserAssignedToProjectDto::projectId eq projectId).toList()

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectCollection.find(UserAssignedToProjectDto::userName eq userName).toList()
}