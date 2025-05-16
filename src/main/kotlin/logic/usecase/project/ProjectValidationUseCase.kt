package org.example.logic.usecase.project

import org.example.logic.repository.ProjectRepository
import java.util.*

class ProjectValidationUseCase(
    private val projectRepository: ProjectRepository,
) {
    suspend fun isProjectNameExists(projectName: String): Boolean {
        return try {
            projectRepository.getProjectByName(projectName)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isProjectExists(projectId: UUID): Boolean {
        return try {
            projectRepository.getProjectById(projectId)
            true
        } catch (e: Exception) {
            false
        }
    }
}