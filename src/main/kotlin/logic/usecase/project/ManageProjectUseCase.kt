package org.example.logic.usecase.project

import logic.model.entities.Project

import org.example.logic.model.exceptions.ProjectNotFoundException
import org.example.logic.model.exceptions.StateNotExistException
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.audit.AddAuditSystemUseCase
import org.example.logic.usecase.state.ManageStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectStateUseCase: ManageStatesUseCase,
    private val addAuditSystemUseCase: AddAuditSystemUseCase
) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects()
    }

    fun getProjectByName(projectName: String): Result<Project> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(it) },
            onSuccess = { allProjects -> getProjectFromList(projectName, allProjects) }
        )
    }

    private fun getProjectFromList(projectName: String, allProjects: List<Project>): Result<Project> {
        return allProjects.find { project ->
            project.name == projectName
        }?.let { foundProject ->
            Result.success(foundProject)
        } ?: Result.failure(ProjectNotFoundException())
    }

    fun addProject(projectName: String, stateName: String): Result<Boolean> {
        val projectStateId = manageProjectStateUseCase.getProjectStateIdByName(stateName)
            ?: return Result.failure(StateNotExistException())

        return projectRepository.addProject(Project(id = UUID.randomUUID(), projectName, projectStateId))
    }


    fun updateProject(projectId: UUID, newProjectName: String, newProjectStateName: String): Result<Boolean> {
        val newProjectStateId = manageProjectStateUseCase.getProjectStateIdByName(newProjectStateName)
            ?: return Result.failure(StateNotExistException())
        return projectRepository.editProjectState(
            Project(
                id = projectId,
                name = newProjectName,
                stateId = newProjectStateId
            )
        )
    }

    fun removeProjectByName(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onFailure = { Result.failure(it) },
            onSuccess = { project -> projectRepository.deleteProject(project) }
        )
    }

    fun isProjectExists(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onSuccess = { Result.success(true) },
            onFailure = { Result.failure(it) }
        )
    }
}