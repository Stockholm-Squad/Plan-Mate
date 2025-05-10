package data.source.remote.mongo

import data.dto.UserDto
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.UserDataSource
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class UserMongoDataSource(
    private val userCollection: CoroutineCollection<UserDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
) : UserDataSource {

    override suspend fun addUser(user: UserDto): Boolean {
        val result = userCollection.insertOne(user)
        return result.wasAcknowledged()
    }

    override suspend fun getAllUsers(): List<UserDto> {
        return userCollection.find().toList()
    }

    override suspend fun getUsersByProjectId(projectId: String): List<UserDto> {
        val assignedUsernames = userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId(projectId)
            .map { it.userName }

        if (assignedUsernames.isEmpty()) return emptyList()

        val filter = UserDto::username `in` assignedUsernames
        return userCollection.find(filter).toList()
    }

    override suspend fun isUserExist(username: String): Boolean {
        val filter = UserDto::username eq username
        return userCollection.findOne(filter) != null
    }

    override suspend fun getUserById(userId: String): UserDto? {
        val filter = UserDto::id eq userId
        return userCollection.findOne(filter)
    }

    override suspend fun editUser(user: UserDto): Boolean {
        val result = userCollection.updateOne(
            filter = UserDto::id eq user.id,
            update = setValue(UserDto::username, user.username)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteUser(user: UserDto): Boolean {
        val result = userCollection.deleteOne(UserDto::id eq user.id)
        return result.deletedCount > 0
    }
}
