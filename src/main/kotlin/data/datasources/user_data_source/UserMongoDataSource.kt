package org.example.data.datasources.user_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.models.UserModel
import org.example.data.utils.USERS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserMongoDataSource(mongoDatabase: CoroutineDatabase) : IUserDataSource {

    private val collection = mongoDatabase.getCollection<UserModel>(USERS_COLLECTION_NAME)


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
