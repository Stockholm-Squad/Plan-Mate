package org.example.data.repo


import data.models.UserAssignedToProject
import logic.model.entities.Project
import logic.model.entities.User
import org.example.data.datasources.models.project_data_source.IProjectDataSource
import org.example.data.datasources.models.user_data_source.IUserDataSource
import org.example.data.datasources.relations.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.mapper.ProjectMapper
import org.example.data.mapper.UserMapper
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: IProjectDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
    private val userDataSource: IUserDataSource,
    private val projectMapper: ProjectMapper,
    private val userMapper: UserMapper
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(projectMapper.mapToProjectModel(project)))
    }

    override fun editProject(updatedProject: Project): Result<Boolean> {
        return projectDataSource.read()
            .fold(onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }, onSuccess = {
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
            .fold(onFailure = { Result.failure(it) },
                onSuccess = { projects ->
                    projects.filterNot { project -> project.id == projectToDelete.id.toString() }
                        .let { projectList -> projectDataSource.overWrite(projectList) }

                })
    }

    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read().fold(
            onSuccess = { list -> Result.success(list.map { it1 -> projectMapper.mapToProjectEntity(it1) }) },
            onFailure = { Result.failure(it) }
        )
    }


    override fun getUsersAssignedToProject(projectId: String): Result<List<User>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { userAssignedToProject ->
                userDataSource.read().fold(
                    onSuccess = { users ->
                        userAssignedToProject.filter {
                            projectId == it.projectId
                        }.map { it.userName }
                            .let { userNames ->
                                users.filter { user -> userNames.contains(user.username) }.map { userModel ->
                                    userMapper.mapToUserEntity(userModel)
                                }.let {
                                    Result.success(it)
                                }
                            }
                    },
                    onFailure = { Result.failure(it) }
                )

            }, onFailure = { Result.failure(it) })
    }

    override fun addUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.append(
            listOf(UserAssignedToProject(projectId = projectId, userName = userName))
        )
    }

    override fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.read().fold(onSuccess = { usersAssignedToProject ->
            usersAssignedToProject.filterNot { userAssignedToProject ->
                (userAssignedToProject.projectId == projectId) && (userAssignedToProject.userName == userName)
            }.let { newUsersAssignedToProject ->
                userAssignedToProjectDataSource.overWrite(newUsersAssignedToProject)
            }
        }, onFailure = { Result.failure(it) })
    }

    override fun getProjectsAssignedToUser(userName: String): Result<List<Project>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { userAssignments ->
                projectDataSource.read().fold(
                    onSuccess = { projects ->
                        userAssignments.filter { it.userName == userName }
                            .map { it.projectId }
                            .let { projectIds ->
                                projects.filter { project -> projectIds.contains(project.id) }
                                    .map { projectModel -> projectMapper.mapToProjectEntity(projectModel) }
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
