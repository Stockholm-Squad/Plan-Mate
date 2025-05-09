package org.example.data.datasources.user_assigned_to_project_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.models.UserAssignedToProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.*

class UserAssignedToProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : UserAssignedToProjectDataSource {

    private val collection = mongoDatabase.getCollection<UserAssignedToProjectModel>(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME)

    override suspend fun addUserToProject(projectId: String, userName: String): Boolean {
        val document = UserAssignedToProjectModel(
            userName = userName,
            projectId = projectId
        )

        val result = collection.insertOne(document)
        return result.wasAcknowledged()
    }

    override suspend fun deleteUserFromProject(projectId: String, userName: String): Boolean {
        val filter = and(
            UserAssignedToProjectModel::userName eq userName,
            UserAssignedToProjectModel::projectId eq projectId
        )

        val result = collection.deleteOne(filter)
        return result.deletedCount > 0
    }

    override suspend fun getUsersAssignedToProjectByProjectId(projectId: String): List<UserAssignedToProjectModel> {
        val filter = UserAssignedToProjectModel::projectId eq projectId
        return collection.find(filter).toList()
    }

    override suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectModel> {
        val filter = UserAssignedToProjectModel::userName eq userName
        return collection.find(filter).toList()
    }
}
