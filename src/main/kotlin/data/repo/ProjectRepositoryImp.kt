package org.example.data.repo

import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.source.ProjectDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.NoProjectAddedException
import org.example.logic.NoProjectDeletedException
import org.example.logic.NoProjectEditedException
import org.example.logic.NoProjectsFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: ProjectDataSource,
) : ProjectRepository {

    override suspend fun getProjectsByUsername(username: String): List<Project> {
        return tryToExecute(
            { projectDataSource.getProjectsByUsername(username) },
            onSuccess = { listOfProjects ->
                listOfProjects.mapNotNull { project -> project.mapToProjectEntity() }
            },
            onFailure = { throw NoProjectsFoundException() },
        )
    }


    override suspend fun addProject(project: Project): Boolean {
        return tryToExecute(
            { projectDataSource.addProject(project.mapToProjectModel()) },
            onSuccess = { success ->
                success
            }, onFailure = { throw NoProjectAddedException() })
    }

    override suspend fun editProject(updatedProject: Project): Boolean {
        return tryToExecute(
            { projectDataSource.editProject(updatedProject.mapToProjectModel()) },
            onSuccess = { success ->
                success
            }, onFailure = {
                throw NoProjectEditedException()
            })
    }

    override suspend fun deleteProject(projectToDelete: Project): Boolean {
        return tryToExecute(
            { projectDataSource.deleteProject(projectToDelete.mapToProjectModel()) },
            onSuccess = { success ->
                success
            },
            onFailure = {
                throw NoProjectDeletedException()
            })
    }

    override suspend fun getAllProjects(): List<Project> {
        return tryToExecute(
            { projectDataSource.getAllProjects() },
            onSuccess = { listOfProjects ->
                listOfProjects.mapNotNull { projectModel -> projectModel.mapToProjectEntity() }
            },
            onFailure = { throw NoProjectsFoundException() },
        )
    }

}