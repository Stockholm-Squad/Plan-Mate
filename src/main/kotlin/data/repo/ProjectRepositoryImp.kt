package org.example.data.repo


import logic.model.entities.Project
import org.example.data.datasources.project_data_source.IProjectDataSource
import org.example.data.datasources.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.mapper.mapToProjectEntity
import org.example.data.mapper.mapToProjectModel
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: IProjectDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(project.mapToProjectModel()))
    }

    override fun editProject(updatedProject: Project): Result<Boolean> {
        return projectDataSource.read()
            .fold(onFailure = { Result.failure(ReadDataException()) }, onSuccess = {
                it.map { project ->
                    if (project.id == updatedProject.id.toString()) {
                        updatedProject
                    } else project
                }
                projectDataSource.overWrite(it)
            })
    }

    override fun deleteProject(projectToDelete: Project): Result<Boolean> {
        return projectDataSource.read()
            .fold(
                onFailure = { Result.failure(it) },
                onSuccess = { projects ->
                    projects.filterNot { project -> project.id == projectToDelete.id.toString() }
                        .let { projectList -> projectDataSource.overWrite(projectList) }

                })
    }

    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read().fold(
            onSuccess = { list -> Result.success(list.mapNotNull { it.mapToProjectEntity() }) },
            onFailure = { Result.failure(it) }
        )
    }

    override fun getProjectsByUsername(username: String): Result<List<Project>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { userAssignments ->
                projectDataSource.read().fold(
                    onSuccess = { projects ->
                        userAssignments.filter { it.userName == username }
                            .map { it.projectId }
                            .let { projectIds ->
                                projects.filter { project -> projectIds.contains(project.id) }
                                    .mapNotNull { it.mapToProjectEntity() }
                                    .let { Result.success(it) }
                            }
                    },
                    onFailure = { Result.failure(it) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}
