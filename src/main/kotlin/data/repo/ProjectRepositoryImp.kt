package org.example.data.repo


import data.models.UserAssignedToProjectModel
import logic.models.entities.Project
import org.example.data.datasources.project_data_source.IProjectDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.data.models.ProjectModel
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: IProjectDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(project.mapToProjectModel()))
    }

    override fun editProject(updatedProject: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onSuccess = { existingProjects ->
                updateProjectList(existingProjects, updatedProject)
                    .let { updatedProjects -> projectDataSource.overWrite(updatedProjects) }
            },
            onFailure = { Result.failure(it) }
        )
    }

    private fun updateProjectList(
        existing: List<ProjectModel>,
        updated: Project
    ): List<ProjectModel> {
        return existing.map {
            if (it.id == updated.id.toString()) updated.mapToProjectModel() else it
        }
    }

    override fun deleteProject(projectToDelete: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onSuccess = { projects ->
                projects.filterNot { project ->
                    project.id == projectToDelete.id.toString()
                }.let { projectList ->
                    projectDataSource.overWrite(projectList)
                }
            },
            onFailure = { Result.failure(it) },
        )
    }

    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read().fold(
            onSuccess = { projectModelList ->
                Result.success(
                    projectModelList.mapNotNull { projectModel ->
                        projectModel.mapToProjectEntity()
                    },
                )
            },
            onFailure = { Result.failure(it) }
        )
    }

    override fun getProjectsByUsername(username: String): Result<List<Project>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { assignments ->
                return filterAssignmentsByUsername(assignments, username).let { filteredAssignments ->
                    extractProjectIds(filteredAssignments).let { projectIds ->
                        projectDataSource.read().fold(
                            onSuccess = { projects ->
                                filterProjectsByIds(projects, projectIds).let { filteredProjects ->
                                    Result.success(convertToProjects(filteredProjects))
                                }
                            },
                            onFailure = { Result.failure(it) }
                        )
                    }
                }
            },
            onFailure = { Result.failure(it) }
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

    private fun filterProjectsByIds(
        projects: List<ProjectModel>,
        ids: List<String>
    ): List<ProjectModel> {
        return projects.filter { ids.contains(it.id) }
    }

    private fun convertToProjects(projects: List<ProjectModel>): List<Project> {
        return projects.mapNotNull { it.mapToProjectEntity() }
    }

}
