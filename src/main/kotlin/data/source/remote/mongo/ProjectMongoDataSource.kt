package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.ProjectDto
import data.dto.UserAssignedToProjectDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.ProjectDataSource
import org.example.logic.ProjectNotFoundException
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class ProjectMongoDataSource(
    private val projectCollection: MongoCollection<ProjectDto>,
    private val userAssignedToProjectCollection: MongoCollection<UserAssignedToProjectDto>,
    ) : ProjectDataSource {

    override suspend fun addProject(project: ProjectDto): Boolean =
        projectCollection.insertOne(project).insertedId != null

    override suspend fun updateProject(projectToUpdate: ProjectDto): Boolean =
        projectCollection.updateOne(
            ProjectDto::id eq projectToUpdate.id,
            listOf(
                setValue(ProjectDto::title, projectToUpdate.title),
                setValue(ProjectDto::stateId, projectToUpdate.stateId)
            )
        ).matchedCount > 0

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean =
        projectCollection.deleteOne(ProjectDto::id eq projectToDelete.id).deletedCount > 0

    override suspend fun getAllProjects(): List<ProjectDto> = projectCollection.find().toList()

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> =
        projectCollection.find(
            ProjectDto::id `in` getUsersAssignedToProjectByUserName(username)
                .map { userToProject -> userToProject.projectId }
        ).toList()

    override suspend fun getProjectByName(projectName: String): ProjectDto {
        return projectCollection.find(ProjectDto::title eq projectName).firstOrNull()
            ?: throw ProjectNotFoundException()
    }

    override suspend fun getProjectById(projectId: String): ProjectDto {
        return projectCollection.find(ProjectDto::id eq projectId).firstOrNull()
            ?: throw ProjectNotFoundException()
    }

    private suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto> =
        userAssignedToProjectCollection.find(UserAssignedToProjectDto::username eq userName).toList()
}