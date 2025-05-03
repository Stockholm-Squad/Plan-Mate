package org.example.logic.usecase.project

import logic.model.entities.Project
import org.example.data.extention.toSafeUUID
import org.example.logic.model.exceptions.InvalidPassword
import org.example.logic.model.exceptions.NoObjectFound
import org.example.logic.model.exceptions.NoProjectAdded
import org.example.logic.repository.ProjectRepository
import java.awt.dnd.InvalidDnDOperationException
import java.util.*

class ManageProjectUseCase(private val projectRepository: ProjectRepository) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { Result.success(it) }
        )
    }

    fun getProjectById(id: String): Result<Project> {
        kotlin.runCatching { id.toSafeUUID() }.fold(
            onSuccess = {
                projectRepository.getAllProjects().fold(
                    onFailure = { Result.failure(NoObjectFound()) },
                    onSuccess = { allProjects -> findProject(id, allProjects) }
                )
            },
            onFailure = {Result.failure(InvalidPassword)}
        )
    }

    private fun findProject(id: UUID, allProjects: List<Project>): Result<Project> {
        return allProjects.find { it.id == id }?.let { Result.success(it) }
            ?: Result.failure(NoObjectFound())

    }
    //ToDO add task, add state
    fun addProject(project: Project): Result<Boolean> {
        return projectRepository.addProject(project).fold(
            onFailure = { Result.failure(NoProjectAdded()) },
            onSuccess = { Result.success(true) }
        )
    }


    fun updateProject(project: Project): Result<Boolean> {
        return projectRepository.editProject(project).fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { Result.success(true) }
        )
    }

    fun removeProjectById(projectId: UUID): Result<Boolean> {
        return getProjectById(projectId).fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { project -> Result.success(true).let { projectRepository.deleteProject(project) } }
        )
    }

    fun isProjectExists(projectId: UUID): Result<Boolean> {
        return getProjectById(projectId).fold(
            onSuccess = { Result.success(true) },
            onFailure = { exception -> Result.failure(exception) }
        )
    }
}