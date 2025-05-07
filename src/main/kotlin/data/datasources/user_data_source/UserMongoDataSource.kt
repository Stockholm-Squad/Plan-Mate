package org.example.data.datasources.user_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.models.UserModel
import org.example.data.database.USERS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserMongoDataSource(mongoDatabase: CoroutineDatabase) : IUserDataSource {

    private val collection = mongoDatabase.getCollection<UserModel>(USERS_COLLECTION_NAME)


    override suspend fun read(): List<UserModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<UserModel>): Boolean = withContext(Dispatchers.IO) {
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

    override suspend fun append(users: List<UserModel>): Boolean = withContext(Dispatchers.IO) {
        if (users.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(users)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == users.size
    }
}
