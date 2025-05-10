package org.example.data.source.remote

import data.dto.ProjectDto
import org.example.data.source.ProjectDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class ProjectMongoDataSource(
    private val projectCollection: CoroutineCollection<ProjectDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource
) : ProjectDataSource {

    override suspend fun addProject(project: ProjectDto): Boolean {
        val result = projectCollection.insertOne(project)
        return result.wasAcknowledged()
    }

    override suspend fun editProject(updatedProject: ProjectDto): Boolean {
        val result = projectCollection.updateOne(
            filter = ProjectDto::id eq updatedProject.id,
            update = setValue(ProjectDto::name, updatedProject.name)
        )
        return result.matchedCount > 1
    }

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean {
        val result = projectCollection.deleteOne(ProjectDto::id eq projectToDelete.id)
        return result.deletedCount > 0
    }

    override suspend fun getAllProjects(): List<ProjectDto> {
        return projectCollection.find().toList()
    }

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> {
        val projectIds = userAssignedToProjectDataSource.getUsersAssignedToProjectByUserName(username).map {
            it.projectId
        }
        val filter = ProjectDto::id `in` projectIds
        return projectCollection.find(filter).toList()
    }

}
