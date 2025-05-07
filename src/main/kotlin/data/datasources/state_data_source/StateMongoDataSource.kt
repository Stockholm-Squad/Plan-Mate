package org.example.data.datasources.state_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.database.STATES_COLLECTION_NAME
import org.example.data.models.ProjectStateModel
import org.litote.kmongo.coroutine.CoroutineDatabase


class StateMongoDataSource(mongoDatabase: CoroutineDatabase) : IStateDataSource {

    private val collection = mongoDatabase.getCollection<ProjectStateModel>(STATES_COLLECTION_NAME)

    override suspend fun read(): List<ProjectStateModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(state: List<ProjectStateModel>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (state.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(state)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == state.size
    }

    override suspend fun append(state: List<ProjectStateModel>): Boolean = withContext(Dispatchers.IO) {
        if (state.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(state)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == state.size
    }
}
