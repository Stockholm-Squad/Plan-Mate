package org.example.logic.usecase.project

import logic.models.entities.Project
import logic.models.exceptions.ProjectNotFoundException
import org.example.logic.repository.ProjectRepository

class GetProjectsUseCase(
    private val projectRepository: ProjectRepository,
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
}