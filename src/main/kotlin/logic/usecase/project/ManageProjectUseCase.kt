package org.example.logic.usecase.project

import org.example.logic.ProjectAlreadyExistException
import org.example.logic.ProjectNotFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageEntityStatesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
) {

    suspend fun addProject(projectName: String, stateName: String): Boolean {
        return isProjectNameExists(projectName).let { isProjectNameExists ->
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
        return isProjectExists(projectId).let { isProjectExist ->
            if (isProjectExist) {
                val newProjectStateId = manageProjectStateUseCase.getEntityStateIdByName(newProjectStateName)
                val updatedProject = Project(id = projectId, name = newProjectName, stateId = newProjectStateId)
                projectRepository.UpdatedProject(updatedProject)
            } else
                throw ProjectNotFoundException()
        }
    }

    suspend fun removeProjectByName(projectName: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let {
            projectRepository.deleteProject(it)
        }
    }

    suspend fun isProjectNameExists(projectName: String): Boolean {
        return try {
            getProjectsUseCase.getProjectByName(projectName)
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun isProjectExists(projectId: UUID): Boolean {
        return try {
            getProjectsUseCase.getAllProjects().any { project -> project.id == projectId }
        } catch (e: Exception) {
            false
        }
    }

}