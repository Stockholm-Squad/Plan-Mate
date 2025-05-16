package org.example.logic.usecase.project

import org.example.logic.ProjectAlreadyExistException
import org.example.logic.ProjectNotFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.state.ManageEntityStatesUseCase
import java.util.UUID

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageEntityStatesUseCase,
    private val projectValidationUseCase: ProjectValidationUseCase,
) {

    suspend fun addProject(projectName: String, stateName: String): Boolean {
        return projectValidationUseCase.isProjectNameExists(projectName).let { isProjectNameExists ->
            if (!isProjectNameExists) {
                manageProjectStateUseCase.getEntityStateIdByName(stateName).let { projectStateId ->
                    projectRepository.addProject(
                        Project(
                            id = UUID.randomUUID(),
                            title = projectName,
                            stateId = projectStateId
                        )
                    )
                }
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
        return projectValidationUseCase.isProjectExists(projectId).let { isProjectExist ->
            if (isProjectExist) {
                manageProjectStateUseCase.getEntityStateIdByName(newProjectStateName).let { newProjectStateId ->
                    projectRepository.updateProject(
                        Project(
                            id = projectId,
                            title = newProjectName,
                            stateId = newProjectStateId
                        )
                    )
                }
            } else
                throw ProjectNotFoundException()
        }
    }

    suspend fun removeProjectByName(projectName: String): Boolean {
        return projectRepository.getProjectByName(projectName).let {
            projectRepository.deleteProject(it)
        }
    }
}


