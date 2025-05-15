package org.example.logic.usecase.project

import java.util.UUID

class ProjectValidation(
    private val userProjectManagementUseCase: UserProjectManagementUseCase
) {


    suspend fun isProjectNameExists(projectName: String): Boolean {
        return try {
            userProjectManagementUseCase.getProjectByName(projectName)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isProjectExists(projectId: UUID): Boolean {
        return try {
            userProjectManagementUseCase.getAllProjects().any { project -> project.id == projectId }
        } catch (e: Exception) {
            false
        }
    }
}