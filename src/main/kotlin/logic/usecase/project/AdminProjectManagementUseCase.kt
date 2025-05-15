package org.example.logic.usecase.project

import logic.usecase.login.LoginUseCase
import org.example.logic.ProjectAlreadyExistException
import org.example.logic.ProjectNotFoundException
import org.example.logic.UserDoesNotExistException
import org.example.logic.entities.Project
import org.example.logic.entities.User
import org.example.logic.repository.ProjectRepository
import org.example.logic.repository.UserRepository
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import java.util.UUID

class AdminProjectManagementUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageEntityStatesUseCase,
    private val projectValidation: ProjectValidation,
    private val userProjectManagementUseCase: UserProjectManagementUseCase,
    private val loginUseCase: LoginUseCase,
    private val userRepository: UserRepository
) {

    suspend fun addProject(projectName: String, stateName: String): Boolean {
        return projectValidation.isProjectNameExists(projectName).let { isProjectNameExists ->
            if (!isProjectNameExists) {
                val projectStateId = manageProjectStateUseCase.getEntityStateIdByName(stateName)
                val newProject = Project(id = UUID.randomUUID(), projectName, projectStateId)

                projectRepository.addProject(newProject)
            } else {
                throw ProjectAlreadyExistException()
            }

        }
    }

    suspend fun updateProject(
        projectId: UUID,
        newProjectName: String,
        newProjectStateName: String,
    ): Boolean {
        return projectValidation.isProjectExists(projectId).let { isProjectExist ->
            if (isProjectExist) {
                val newProjectStateId = manageProjectStateUseCase.getEntityStateIdByName(newProjectStateName)
                val updatedProject = Project(id = projectId, title = newProjectName, stateId = newProjectStateId)
                projectRepository.updateProject(updatedProject)
            } else
                throw ProjectNotFoundException()
        }
    }

    suspend fun removeProjectByName(projectName: String): Boolean {
        return userProjectManagementUseCase.getProjectByName(projectName).let {
            projectRepository.deleteProject(it)
        }
    }

    suspend fun getUsersByProjectId(projectId: UUID): List<User> {
        return userRepository.getUsersByProjectId(projectId = projectId)
    }


    suspend fun addUserToProject(projectId: UUID, userName: String): Boolean {
        return loginUseCase.isUserExist(userName).let { isUserExist ->
            if (isUserExist)
                userRepository.addUserToProject(projectId = projectId, username = userName)
            else
                throw UserDoesNotExistException()
        }
    }


    suspend fun deleteUserFromProject(projectName: String, username: String): Boolean {
        return userProjectManagementUseCase.getProjectByName(projectName).let { project ->
            loginUseCase.isUserExist(username).let { isUserExist ->
                if (isUserExist)
                    userRepository.deleteUserFromProject(projectId = project.id, username = username)
                else
                    throw UserDoesNotExistException()
            }
        }

    }


}