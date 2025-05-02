package org.example.logic.usecase.project

import logic.model.entities.Project
import org.example.logic.model.exceptions.NoObjectFound
import org.example.logic.model.exceptions.NoProjectAdded
import org.example.logic.repository.ProjectRepository

class ManageProjectUseCase(private val projectRepository: ProjectRepository) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { Result.success(it) }
        )
    }

    fun getProjectById(id: String): Result<Project> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { allProjects -> getProject(id, allProjects) }
        )
    }

    private fun getProject(id: String, allProjects: List<Project>): Result<Project> {
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

    fun removeProjectById(id: String): Result<Boolean> {
        return getProjectById(id).fold(
            onFailure = { Result.failure(NoObjectFound()) },
            onSuccess = { project -> Result.success(true).let { projectRepository.deleteProject(project) } }
        )
    }

    fun isProjectExists(projectId: String): Result<Boolean> {
        return getProjectById(projectId).fold(
            onSuccess = { Result.success(true) },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }
}