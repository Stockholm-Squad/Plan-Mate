package org.example.data.datasources.user_assigned_to_project_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.datasources.task_data_source.ITaskDataSource
import org.example.data.models.UserModel
import org.example.data.utils.USERS_COLLECTION_NAME

class UserAssignedToProjectMongoDataSource() : IUserAssignedToProjectDataSource {

    private val collection = MongoSetup.database.getCollection<UserModel>(USERS_COLLECTION_NAME)


    override suspend fun read(): List<UserModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<UserModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(users)
        true
    }

    override suspend fun append(users: List<UserModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(users)
        true
    }
}
