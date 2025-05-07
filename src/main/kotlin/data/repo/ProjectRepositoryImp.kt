package org.example.data.repo


import data.models.UserAssignedToProjectModel
import logic.models.entities.Project
import logic.models.exceptions.ProjectExceptions
import org.example.data.datasources.project_data_source.IProjectDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.models.ProjectModel
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: IProjectDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
) : ProjectRepository {


    private fun updateProjectList(
        existing: List<ProjectModel>,
        updated: Project
    ): List<ProjectModel> {
        return existing.map {
            if (it.id == updated.id.toString()) updated.mapToProjectModel() else it
        }
    }


    override suspend fun getProjectsByUsername(username: String): List<Project> {
        return executeSafelyWithContext(
            onSuccess = {
                userAssignedToProjectDataSource.read().let { assignments ->
                    filterAssignmentsByUsername(assignments, username).let { filteredAssignments ->
                        extractProjectIds(filteredAssignments).let { projectIds ->
                            getProjectsByIds(projectIds)
                        }
                    }
                }
            },
            onFailure = { throw ProjectExceptions.NoProjectsFoundException() },
        )
    }


    private fun filterAssignmentsByUsername(
        assignments: List<UserAssignedToProjectModel>,
        username: String
    ): List<UserAssignedToProjectModel> {
        return assignments.filter { it.userName == username }
    }

    private fun extractProjectIds(assignments: List<UserAssignedToProjectModel>): List<String> {
        return assignments.map { it.projectId }
    }

    private suspend fun getProjectsByIds(
        ids: List<String>
    ): List<Project> {
        return executeSafelyWithContext(
            onSuccess = {
                projectDataSource.read()
                    .let { projects ->
                        convertToProjects(projects.filter { ids.contains(it.id) })
                    }
            },
            onFailure = { throw ProjectExceptions.NoProjectsFoundException() }
        )

    }

    private fun convertToProjects(projects: List<ProjectModel>): List<Project> {
        return projects.mapNotNull { it.mapToProjectEntity() }
    }

    override suspend fun addProject(project: Project): Boolean {
        return executeSafelyWithContext(
            onSuccess = {
                projectDataSource.append(listOf(project.mapToProjectModel()))
            },
            onFailure = { throw ProjectExceptions.ProjectNotAddedException() }
        )
    }

    override suspend fun editProject(updatedProject: Project): Boolean {
        return executeSafelyWithContext(
            onSuccess = {
                projectDataSource.read().let {
                    updateProjectList(it, updatedProject)
                        .let { updatedProjects -> projectDataSource.overWrite(updatedProjects) }
                }

            }, onFailure = {
                throw ProjectExceptions.ProjectNotEditedException()
            })
    }

    override suspend fun deleteProject(projectToDelete: Project): Boolean {
        return try {
            projectDataSource.read().let { projects ->
                projects.filterNot { project ->
                    project.id == projectToDelete.id.toString()
                }.let { projectList ->
                    projectDataSource.overWrite(projectList)
                }
            }
        } catch (e: Exception) {
            throw ProjectExceptions.ProjectNotDeletedException()
        }
    }

    override suspend fun getAllProjects(): List<Project> {
        return try {
            projectDataSource.read().let { projectModels ->
                projectModels.mapNotNull { projectModel ->
                    projectModel.mapToProjectEntity()
                }
            }
        } catch (e: Exception) {
            throw ProjectExceptions.NoProjectsFoundException()
        }
    }

}