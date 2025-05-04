package org.example.logic.usecase.project

import logic.model.entities.Project

import org.example.logic.model.exceptions.NoObjectFound
import org.example.logic.model.exceptions.NoProjectAdded
import org.example.logic.model.exceptions.StateNotExistException
import org.example.logic.repository.ProjectRepository
import org.example.logic.usecase.state.ManageStatesUseCase
import java.util.*

class ManageProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val manageProjectState: ManageStatesUseCase
) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { Result.success(it) }
        )
    }

    fun getProjectByName(projectName: String): Result<Project> {
        return kotlin.runCatching { projectName }.fold(
            onSuccess = {
                projectRepository.getAllProjects().fold(
                    onFailure = { Result.failure(NoObjectFound()) },
                    onSuccess = { allProjects -> findProject(projectName, allProjects) }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }


    fun addProject(projectName: String, stateName: String): Result<Boolean> {
        val projectStateId = manageProjectState.getProjectStateIdByName(stateName)
            ?: return Result.failure(StateNotExistException())

        return projectRepository.addProject(Project(id = UUID.randomUUID(), projectName, projectStateId)).fold(
            onFailure = { Result.failure(NoProjectAdded()) },
            onSuccess = { Result.success(true) }
        )
    }


    fun updateProject(projectName: String, newProjectStateName: String): Result<Boolean> {
        val newProjectStateId =
            manageProjectState.getProjectStateIdByName(newProjectStateName)
                ?: return Result.failure(StateNotExistException())
        return projectRepository.editProject(Project(name = projectName, stateId = newProjectStateId)).fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { Result.success(true) }
        )
    }

    fun removeProjectById(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { project -> Result.success(true).let { projectRepository.deleteProject(project) } }
        )
    }

    fun isProjectExists(projectName: String): Result<Boolean> {
        return getProjectByName(projectName).fold(
            onSuccess = { Result.success(true) },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    private fun findProject(projectName: String, allProjects: List<Project>): Result<Project> {
        return allProjects.find { it.name == projectName }?.let { Result.success(it) }
            ?: Result.failure(NoObjectFound())

    }
}