package org.example.data.datasources.project_data_source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.data.datasources.MongoSetup
import org.example.data.models.ProjectModel
import org.example.data.utils.PROJECTS_COLLECTION_NAME

class ProjectMongoDataSource() : IProjectDataSource {

    private val collection = MongoSetup.database.getCollection<ProjectModel>(PROJECTS_COLLECTION_NAME)

    override suspend fun read(): List<ProjectModel> = withContext(Dispatchers.IO) {
        collection.find().toList()
    }

    override suspend fun overWrite(projects: List<ProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.deleteMany()
        collection.insertMany(projects)
        true
    }

    override suspend fun append(projects: List<ProjectModel>): Boolean = withContext(Dispatchers.IO) {
        collection.insertMany(projects)
        true
    }
}
