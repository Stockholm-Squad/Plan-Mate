package org.example.data.source.remote

import org.example.data.models.ProjectModel
import org.example.data.database.PROJECTS_COLLECTION_NAME
import org.example.data.source.UserAssignedToProjectDataSource
import org.example.data.source.ProjectDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class ProjectMongoDataSource(
    mongoDatabase: CoroutineDatabase,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource
) : ProjectDataSource {

    private val collection = mongoDatabase.getCollection<ProjectModel>(PROJECTS_COLLECTION_NAME)
    override suspend fun addProject(project: ProjectModel): Boolean {
        val result = collection.insertOne(project)
        return result.wasAcknowledged()
    }

    override suspend fun editProject(updatedProject: ProjectModel): Boolean {
        val result = collection.updateOne(
            filter = ProjectModel::id eq updatedProject.id,
            update = setValue(ProjectModel::name, updatedProject.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteProject(projectToDelete: ProjectModel): Boolean {
        val result = collection.deleteOne(ProjectModel::id eq projectToDelete.id)
        return result.deletedCount > 0
    }

    override suspend fun getAllProjects(): List<ProjectModel> {
        return collection.find().toList()
    }

    override suspend fun getProjectsByUsername(username: String): List<ProjectModel> {
        val projectIds = userAssignedToProjectDataSource.getUsersAssignedToProjectByUserName(username).map {
            it.projectId
        }
        val filter = ProjectModel::id `in` projectIds
        return collection.find(filter).toList()
    }

}
