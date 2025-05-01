package org.example.data.repo

import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import data.models.TaskInProject
import data.models.UserAssignedToProject
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: PlanMateDataSource<Project>,
    private val taskInProjectDataSource: PlanMateDataSource<TaskInProject>,
    private val userAssignedToProjectDataSource: PlanMateDataSource<UserAssignedToProject>
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(project)).fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun editProject(project: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                it.map {
                    if (it.id == project.id) {
                        project
                    } else
                        it
                }
                projectDataSource.overWrite(it)
            }
        )
    }


    override fun deleteProject(project: Project): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = {
                it.filterNot { it.id == project.id }.let { projectList -> projectDataSource.overWrite(projectList) }

            }
        )
    }


    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read()
    }

    override fun getTasksInProject(projectId: String): Result<List<String>> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { taskInProject ->
                taskInProject.filter {
                    projectId == it.projectId
                }.map { it.taskId }.let {
                    Result.success(it)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    override fun addTaskInProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.append(listOf(TaskInProject(projectId = projectId, taskId = taskId)))
    }

    override fun deleteTaskFromProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.read().fold(
            onSuccess = { tasksInProject ->
                tasksInProject.filterNot { taskInProject ->
                    (taskInProject.projectId == projectId) && (taskInProject.taskId == taskId)
                }
                    .let { newTasksInProject -> taskInProjectDataSource.overWrite(newTasksInProject) }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    override fun getUsersAssignedToProject(projectId: String): Result<List<String>> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { userAssignedToProject ->
                userAssignedToProject.filter {
                    projectId == it.projectId
                }.map { it.userName }.let {
                    Result.success(it)
                }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    override fun addUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.append(
            listOf(
                UserAssignedToProject(
                    projectId = projectId,
                    userName = userName
                )
            )
        )
    }

    override fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean> {
        return userAssignedToProjectDataSource.read().fold(
            onSuccess = { usersAssignedToProject ->
                usersAssignedToProject.filterNot { userAssignedToProject ->
                    (userAssignedToProject.projectId == projectId) && (userAssignedToProject.userName == userName)
                }
                    .let { newUsersAssignedToProject ->
                        userAssignedToProjectDataSource.overWrite(
                            newUsersAssignedToProject
                        )
                    }
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }
}
