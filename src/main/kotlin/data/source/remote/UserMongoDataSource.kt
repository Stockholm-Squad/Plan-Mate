package org.example.data.source.remote

import org.example.data.source.UserDataSource
import org.example.data.utils.USERS_COLLECTION_NAME
import org.example.data.source.UserAssignedToProjectDataSource
import data.dto.UserModel
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class UserMongoDataSource(
    mongoDatabase: CoroutineDatabase,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
) : UserDataSource {

    private val collection = mongoDatabase.getCollection<UserModel>(USERS_COLLECTION_NAME)

    override suspend fun addUser(user: UserModel): Boolean {
        val result = collection.insertOne(user)
        return result.wasAcknowledged()
    }

    override suspend fun getAllUsers(): List<UserModel> {
        return collection.find().toList()
    }

    override suspend fun getUsersByProjectId(projectId: String): List<UserModel> {
        val assignedUsernames = userAssignedToProjectDataSource.getUsersAssignedToProjectByProjectId(projectId)
            .map { it.userName }

        if (assignedUsernames.isEmpty()) return emptyList()

        val filter = UserModel::username `in` assignedUsernames
        return collection.find(filter).toList()
    }

    override suspend fun isUserExist(username: String): Boolean {
        val filter = UserModel::username eq username
        return collection.findOne(filter) != null
    }

    override suspend fun getUserById(userId: String): UserModel? {
        val filter = UserModel::id eq userId
        return collection.findOne(filter)
    }

    override suspend fun editUser(user: UserModel): Boolean {
        val result = collection.updateOne(
            filter = UserModel::id eq user.id,
            update = setValue(UserModel::username, user.username)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteUser(user: UserModel): Boolean {
        val result = collection.deleteOne(UserModel::id eq user.id)
        return result.deletedCount > 0
    }
}
