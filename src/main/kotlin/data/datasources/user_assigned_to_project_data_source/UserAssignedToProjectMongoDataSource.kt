package org.example.data.datasources.user_assigned_to_project_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import data.models.UserAssignedToProjectModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserAssignedToProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : IUserAssignedToProjectDataSource {

    private val collection = mongoDatabase.getCollection<UserAssignedToProjectModel>(USER_ASSIGNED_TO_PROJECT_COLLECTION_NAME)


    override suspend fun read(): List<UserAssignedToProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<UserAssignedToProjectModel>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (users.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(users)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == users.size
    }

    override suspend fun append(users: List<UserAssignedToProjectModel>): Boolean = withContext(Dispatchers.IO) {
        if (users.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(users)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == users.size
    }
}
