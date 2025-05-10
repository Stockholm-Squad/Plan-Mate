package org.example.data.source.remote

import data.dto.ProjectDto
import org.example.data.utils.PROJECTS_COLLECTION_NAME
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

    private val collection = mongoDatabase.getCollection<ProjectDto>(PROJECTS_COLLECTION_NAME)
    override suspend fun addProject(project: ProjectDto): Boolean {
        val result = collection.insertOne(project)
        return result.wasAcknowledged()
    }

    override suspend fun editProject(updatedProject: ProjectDto): Boolean {
        val result = collection.updateOne(
            filter = ProjectDto::id eq updatedProject.id,
            update = setValue(ProjectDto::name, updatedProject.name)
        )
        return result.matchedCount > 0
    }

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean {
        val result = collection.deleteOne(ProjectDto::id eq projectToDelete.id)
        return result.deletedCount > 0
    }

    override suspend fun getAllProjects(): List<ProjectDto> {
        return collection.find().toList()
    }

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> {
        val projectIds = userAssignedToProjectDataSource.getUsersAssignedToProjectByUserName(username).map {
            it.projectId
        }
        val filter = ProjectDto::id `in` projectIds
        return collection.find(filter).toList()
    }

}
