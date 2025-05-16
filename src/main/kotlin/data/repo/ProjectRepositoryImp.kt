package org.example.data.repo

import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.source.ProjectDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.*
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository
import java.util.UUID

class ProjectRepositoryImp(
    private val projectDataSource: ProjectDataSource
) : ProjectRepository {

    override suspend fun getProjectsByUsername(username: String): List<Project> = tryToExecute(
        function = { projectDataSource.getProjectsByUsername(username) },
        onSuccess = { listOfProjects -> listOfProjects.mapNotNull { project -> project.mapToProjectEntity() } },
        onFailure = { throw NoProjectsFoundException() },
    )

    override suspend fun getProjectByName(projectName: String): Project = tryToExecute(
        function = { projectDataSource.getProjectByName(projectName) },
        onSuccess = { projectModel -> projectModel.mapToProjectEntity() ?: throw ProjectNotFoundException() },
        onFailure = { throw ProjectNotFoundException() },
    )

    override suspend fun getProjectById(projectId: UUID): Project {
        return tryToExecute(
            function = { projectDataSource.getProjectById(projectId.toString()) },
            onSuccess = { projectModel -> projectModel.mapToProjectEntity() ?: throw ProjectNotFoundException() },
            onFailure = { throw ProjectNotFoundException() },
        )
    }

    override suspend fun addProject(project: Project): Boolean = tryToExecute(
        function = { projectDataSource.addProject(project.mapToProjectModel()) },
        onSuccess = { isAdded -> isAdded },
        onFailure = { throw NoProjectAddedException() })


    override suspend fun updateProject(projectToUpdate: Project): Boolean = tryToExecute(
        function = { projectDataSource.updateProject(projectToUpdate.mapToProjectModel()) },
        onSuccess = { isUpdated -> isUpdated },
        onFailure = { throw NoProjectUpdatedException() })


    override suspend fun deleteProject(projectToDelete: Project): Boolean = tryToExecute(
        function = { projectDataSource.deleteProject(projectToDelete.mapToProjectModel()) },
        onSuccess = { isDeleted -> isDeleted },
        onFailure = { throw NoProjectDeletedException() })


    override suspend fun getAllProjects(): List<Project> = tryToExecute(
        function = { projectDataSource.getAllProjects() },
        onSuccess = { listOfProjects -> listOfProjects.mapNotNull { projectModel -> projectModel.mapToProjectEntity() } },
        onFailure = { throw NoProjectsFoundException() },
    )
}