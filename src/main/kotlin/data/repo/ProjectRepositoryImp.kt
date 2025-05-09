package org.example.data.repo

import logic.models.entities.Project
import logic.models.exceptions.ProjectExceptions
import org.example.data.datasources.project_data_source.ProjectDataSource

import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel

import org.example.data.utils.tryToExecute
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: ProjectDataSource,
) : ProjectRepository {

    override suspend fun getProjectsByUsername(username: String): List<Project> {
        return tryToExecute(
            { projectDataSource.getProjectsByUsername(username) },
            onSuccess = {
                it.mapNotNull { it.mapToProjectEntity() }
            },
            onFailure = { throw ProjectExceptions.NoProjectsFoundException() },
        )
    }


    override suspend fun addProject(project: Project): Boolean {
        return tryToExecute({ projectDataSource.addProject(project.mapToProjectModel()) }, onSuccess = {
            it
        }, onFailure = { throw ProjectExceptions.NoProjectAddedException() })
    }

    override suspend fun editProject(updatedProject: Project): Boolean {
        return tryToExecute({ projectDataSource.editProject(updatedProject.mapToProjectModel()) }, onSuccess = {
            it
        }, onFailure = {
            throw ProjectExceptions.NoProjectEditedException()
        })
    }

    override suspend fun deleteProject(projectToDelete: Project): Boolean {
        return tryToExecute(
            { projectDataSource.deleteProject(projectToDelete.mapToProjectModel()) },
            onSuccess = { it },
            onFailure = { throw ProjectExceptions.NoProjectDeletedException() })
    }

    override suspend fun getAllProjects(): List<Project> {
        return tryToExecute(
            { projectDataSource.getAllProjects() },
            onSuccess = {
                it.mapNotNull { projectModel -> projectModel.mapToProjectEntity() }
            },
            onFailure = { throw ProjectExceptions.NoProjectsFoundException() },
        )
    }

}