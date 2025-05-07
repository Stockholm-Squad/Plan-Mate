package org.example.data.datasources.project_data_source

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertManyResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.models.ProjectModel
import org.example.data.database.PROJECTS_COLLECTION_NAME
import org.litote.kmongo.coroutine.CoroutineDatabase

class ProjectMongoDataSource(mongoDatabase: CoroutineDatabase) : IProjectDataSource {

    private val collection = mongoDatabase.getCollection<ProjectModel>(PROJECTS_COLLECTION_NAME)

    override suspend fun read(): List<ProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(projects: List<ProjectModel>): Boolean = withContext(Dispatchers.IO) {
        val deleteResult: DeleteResult = collection.deleteMany()
        if (!deleteResult.wasAcknowledged()) {
            return@withContext false
        }

        if (projects.isEmpty()) {
            return@withContext true
        }

        val insertResult: InsertManyResult = collection.insertMany(projects)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == projects.size
    }

    override suspend fun append(projects: List<ProjectModel>): Boolean = withContext(Dispatchers.IO) {
        if (projects.isEmpty()) {
            return@withContext true
        }
        val insertResult: InsertManyResult = collection.insertMany(projects)
        return@withContext insertResult.wasAcknowledged() && insertResult.insertedIds.size == projects.size
    }
}
