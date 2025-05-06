package org.example.data.datasources.state_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.models.ProjectStateModel
import org.example.data.utils.STATES_COLLECTION_NAME


class StateMongoDataSource() : IStateDataSource {

    private val collection = MongoSetup.database.getCollection<ProjectStateModel>(STATES_COLLECTION_NAME)

    override suspend fun read(): List<ProjectStateModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(users: List<ProjectStateModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(users)
        true
    }

    override suspend fun append(users: List<ProjectStateModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(users)
        true
    }
}
