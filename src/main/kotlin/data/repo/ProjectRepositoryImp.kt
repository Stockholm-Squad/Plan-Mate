package org.example.data.repo

import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.source.ProjectDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.NoProjectAddedException
import org.example.logic.NoProjectDeletedException
import org.example.logic.NoProjectUpdatedException
import org.example.logic.NoProjectsFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: ProjectDataSource
) : ProjectRepository {

    override suspend fun getProjectsByUsername(username: String): List<Project> = tryToExecute(
        function = { projectDataSource.getProjectsByUsername(username) },
        onSuccess = { listOfProjects -> listOfProjects.mapNotNull { project -> project.mapToProjectEntity() } },
        onFailure = { throw NoProjectsFoundException() },
    )


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