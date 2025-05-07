package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.utils.USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserAssignedToProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : IUserAssignedToProjectDataSource {

    private val collection = mongoDatabase.getCollection<UserAssignedToProjectModel>(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME)


    override suspend fun read(): List<UserAssignedToProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<UserAssignedToProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(users)
        true
    }

    override suspend fun append(users: List<UserAssignedToProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(users)
        true
    }
}
