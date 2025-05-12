package data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.UserDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class UserMongoDataSource(
    private val userCollection: MongoCollection<UserDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
) : UserDataSource {

    override suspend fun addUser(user: UserDto): Boolean =
        userCollection.insertOne(user).insertedId != null

    override suspend fun getAllUsers(): List<UserDto> =
        userCollection.find().toList()

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> =
        userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId(projectId)
            .map { userToProject -> userToProject.userName }
            .takeIf { userNames -> userNames.isNotEmpty() }
            ?.let { userCollection.find(UserDto::username `in` it).toList() }
            ?: emptyList()

    override suspend fun isUserExist(username: String): Boolean =
        userCollection.find(Filters.eq(UserDto::username.name, username)).firstOrNull() != null

    override suspend fun getUserById(userId: String): UserDto? =
        userCollection.find(Filters.eq(UserDto::id.name, userId)).firstOrNull()

    override suspend fun editUser(user: UserDto): Boolean =
        userCollection.updateOne(
            UserDto::id eq user.id,
            setValue(UserDto::username, user.username)
        ).matchedCount > 0

    override suspend fun deleteUser(user: UserDto): Boolean =
        userCollection.deleteOne(UserDto::id eq user.id).deletedCount > 0
}