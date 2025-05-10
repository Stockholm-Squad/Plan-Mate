package org.example.logic.usecase.project

import org.example.logic.ProjectNotFoundException
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository

class GetProjectsUseCase(
    private val projectRepository: ProjectRepository,
) {

    suspend fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
    }

    suspend fun getProjectByName(projectName: String): Project {
        return getProjectFromList(
            projectName,
            projectRepository.getAllProjects(),
        )
    }

    private fun getProjectFromList(projectName: String, allProjects: List<Project>): Project {
        return allProjects.find { project ->
            project.name == projectName
        } ?: throw ProjectNotFoundException()
    }
}