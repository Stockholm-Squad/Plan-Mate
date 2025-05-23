package data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.MateTaskAssignmentDto
import data.dto.UserAssignedToProjectDto
import data.dto.UserDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.UserDataSource
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class UserMongoDataSource(
    private val userCollection: MongoCollection<UserDto>,
    private val mateTaskAssignmentCollection: MongoCollection<MateTaskAssignmentDto>,
    private val userAssignedToProjectCollection: MongoCollection<UserAssignedToProjectDto>,
) : UserDataSource {

    override suspend fun addUser(user: UserDto): Boolean = userCollection.insertOne(user).insertedId != null

    override suspend fun getAllUsers(): List<UserDto> = userCollection.find().toList()

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> =
        getUsersAssignedToProjectByProjectId(projectId)
            .map { userToProject -> userToProject.username }
            .takeIf { userNames -> userNames.isNotEmpty() }
            ?.let { userCollection.find(UserDto::username `in` it).toList() }
            ?: emptyList()

    override suspend fun isUserExist(username: String): Boolean =
        userCollection.find(Filters.eq(UserDto::username.name, username)).firstOrNull() != null

    override suspend fun getUserById(userId: String): UserDto? =
        userCollection.find(Filters.eq(UserDto::id.name, userId)).firstOrNull()

    override suspend fun updateUser(user: UserDto): Boolean =
        userCollection.updateOne(
            UserDto::id eq user.id,
            setValue(UserDto::username, user.username)
        ).matchedCount > 0

    override suspend fun deleteUser(user: UserDto): Boolean =
        userCollection.deleteOne(UserDto::id eq user.id).deletedCount > 0

    override suspend fun addUserToTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentCollection.insertOne(
            MateTaskAssignmentDto(
                username = username,
                taskId = taskId
            )
        ).insertedId != null

    override suspend fun deleteUserFromTask(username: String, taskId: String): Boolean =
        mateTaskAssignmentCollection.deleteOne(
            and(
                MateTaskAssignmentDto::username eq username,
                MateTaskAssignmentDto::taskId eq taskId
            )
        ).deletedCount > 0

    override suspend fun addUserToProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectCollection.insertOne(
            UserAssignedToProjectDto(
                username = userName,
                projectId = projectId
            )
        ).insertedId != null

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean =
        userAssignedToProjectCollection.deleteOne(
            and(
                UserAssignedToProjectDto::username eq userName,
                UserAssignedToProjectDto::projectId eq projectId
            )
        ).deletedCount > 0

    private suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectCollection.find(UserAssignedToProjectDto::projectId eq projectId).toList()

    override suspend fun getUserByUsername(username: String): UserDto? =
        userCollection.find(Filters.eq(UserDto::username.name, username)).firstOrNull()

}