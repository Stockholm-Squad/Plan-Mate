package org.example.data.repo


import data.models.TaskInProject
import data.models.UserAssignedToProject
import logic.model.entities.Project
import org.example.data.datasources.models.project_data_source.IProjectDataSource
import org.example.data.datasources.relations.task_In_project_data_source.ITaskInProjectDataSource
import org.example.data.datasources.relations.user_assigned_to_project_data_source.IUserAssignedToProjectDataSource
import org.example.data.mapper.ProjectMapper
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: IProjectDataSource,
    private val taskInProjectDataSource: ITaskInProjectDataSource,
    private val userAssignedToProjectDataSource: IUserAssignedToProjectDataSource,
    private val projectMapper: ProjectMapper
) : ProjectRepository {


    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.append(listOf(projectMapper.mapToProjectModel(project)))
            .fold(onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) },
                onSuccess = { Result.success(true) })
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
            .fold(onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) },
                onSuccess = { projects ->
                    projects.filterNot { project -> project.id == projectToDelete.id.toString() }
                        .let { projectList -> projectDataSource.overWrite(projectList) }

                })
    }


    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read()
            .fold(onSuccess = { list -> Result.success(list.map { it1 -> projectMapper.mapToProjectEntity(it1) }) },
                onFailure = { Result.failure(it) })
    }

    override fun getTasksInProject(projectId: String): Result<List<String>> {
        return taskInProjectDataSource.read().fold(onSuccess = { taskInProject ->
            taskInProject.filter {
                projectId == it.projectId
            }.map { it.taskId }.let {
                Result.success(it)
            }
        }, onFailure = { throwable -> Result.failure(throwable) })
    }

    override fun addTaskInProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.append(listOf(TaskInProject(projectId = projectId, taskId = taskId)))
    }

    override fun deleteTaskFromProject(projectId: String, taskId: String): Result<Boolean> {
        return taskInProjectDataSource.read().fold(onSuccess = { tasksInProject ->
            tasksInProject.filterNot { taskInProject ->
                (taskInProject.projectId == projectId) && (taskInProject.taskId == taskId)
            }.let { newTasksInProject -> taskInProjectDataSource.overWrite(newTasksInProject) }
        }, onFailure = { throwable -> Result.failure(throwable) })
    }

    override fun getUsersAssignedToProject(projectId: String): Result<List<String>> {
        return userAssignedToProjectDataSource.read().fold(onSuccess = { userAssignedToProject ->
            userAssignedToProject.filter {
                projectId == it.projectId
            }.map { it.userName }.let {
                Result.success(it)
            }
        }, onFailure = { throwable -> Result.failure(throwable) })
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
        }, onFailure = { throwable -> Result.failure(throwable) })
    }
}
