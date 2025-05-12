package data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import data.dto.ProjectDto
import kotlinx.coroutines.flow.toList
import org.example.data.source.ProjectDataSource
import org.example.data.source.UserAssignedToProjectDataSource
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.setValue

class ProjectMongoDataSource(
    private val projectCollection: MongoCollection<ProjectDto>,
    private val userAssignedToProjectDataSource: UserAssignedToProjectDataSource,
) : ProjectDataSource {

    override suspend fun addProject(project: ProjectDto): Boolean =
        projectCollection.insertOne(project).insertedId != null

    override suspend fun updateProject(projectToUpdate: ProjectDto): Boolean =
        projectCollection.updateOne(
            ProjectDto::id eq projectToUpdate.id,
            setValue(ProjectDto::name, projectToUpdate.name)
        ).matchedCount > 0

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean =
        projectCollection.deleteOne(ProjectDto::id eq projectToDelete.id).deletedCount > 0

    override suspend fun getAllProjects(): List<ProjectDto> = projectCollection.find().toList()

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> =
        projectCollection.find(
            ProjectDto::id `in` userAssignedToProjectDataSource
                .getUsersAssignedToProjectByUserName(username)
                .map { userToProject -> userToProject.projectId }
        ).toList()
}