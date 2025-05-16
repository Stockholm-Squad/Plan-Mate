package org.example.logic.usecase.project

import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository

class GetProjectsUseCase(
    private val projectRepository: ProjectRepository,
) {

    suspend fun getAllProjects(): List<Project> {
        return projectRepository.getAllProjects()
    }

    suspend fun getProjectByName(projectName: String): Project {
        return projectRepository.getProjectByName(projectName)
    }
}