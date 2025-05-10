package org.example.logic.usecase.project

import org.example.logic.ProjectAlreadyExistException
import org.example.logic.ProjectNotFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageStatesUseCase,
    private val getProjectsUseCase: GetProjectsUseCase,
) {

    suspend fun addProject(projectName: String, stateName: String): Boolean {
        return isProjectExists(projectName).let { success ->
            if (!success) {
                val projectStateId = manageProjectStateUseCase.getProjectStateIdByName(stateName)
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
        return isProjectExists(newProjectName).let { success ->
            if (success) {
                val newProjectStateId = manageProjectStateUseCase.getProjectStateIdByName(newProjectStateName)
                val updatedProject = Project(id = projectId, name = newProjectName, stateId = newProjectStateId)
                projectRepository.editProject(updatedProject)
            } else
                throw ProjectNotFoundException()
        }
    }

    suspend fun removeProjectByName(projectName: String): Boolean {
        return getProjectsUseCase.getProjectByName(projectName).let {
            projectRepository.deleteProject(it)
        }
    }

    suspend fun isProjectExists(projectName: String): Boolean {
        return try {
            getProjectsUseCase.getProjectByName(projectName)
            true
        } catch (e: Exception) {
            false
        }
    }


}