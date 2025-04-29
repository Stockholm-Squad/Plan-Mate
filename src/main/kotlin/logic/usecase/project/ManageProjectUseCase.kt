package org.example.logic.usecase.project

import logic.model.entities.Project
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ManageProjectUseCase(private val projectRepository: ProjectRepository) {

    fun getAllProjects(): Result<List<Project>> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoObjectFound()) },
            onSuccess = { Result.success(it) }
        )
    }

    fun getProjectById(id: String): Result<Project> {
        return projectRepository.getAllProjects().fold(
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoObjectFound()) },
            onSuccess = { allProjects -> Result.success(getProject(id, allProjects)) }
        )
    }

    private fun getProject(id: String, allProjects: List<Project>): Project {
        return allProjects.find { it.id == id } ?: throw PlanMateExceptions.LogicException.NoObjectFound()

    }

    //ToDO add task, add state
    fun addProject(project: Project): Result<Boolean> {
        return projectRepository.addProject(project).fold(
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoProjectAdded()) },
            onSuccess = { Result.success(true) }
        )
    }


    fun updateProject(id: String): Result<Boolean> {
        return getProjectById(id).fold(
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoObjectFound()) },
            onSuccess = { project -> Result.success(true).onSuccess { projectRepository.editProject(project) } }
        )
    }

    fun removeProjectById(id: String): Result<Boolean> {
        return getProjectById(id).fold(
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NoObjectFound()) },
            onSuccess = { project -> Result.success(true).onSuccess { projectRepository.deleteProject(project) } }
        )
    }


    fun assignUsersToTask() {
        TODO()
    }

}