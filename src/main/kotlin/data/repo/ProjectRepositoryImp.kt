package org.example.data.repo

import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: PlanMateDataSource<Project>
) : ProjectRepository {

    private var projectList: List<Project>? = null

    override fun getProjectById(id: String): Result<Project> {
        return projectDataSource.read().fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = { projectsList -> Result.success(projectsList[0]) }
        )
    }

    override fun addProject(project: Project): Result<Boolean> {
        return projectDataSource.write(listOf(project)).fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun editProject(project: Project): Result<Boolean> {
        return projectDataSource.write(listOf(project)).fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun deleteProject(id: String): Result<Boolean> {
        return projectDataSource.read().fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = { Result.success(true) }
        )
    }

    override fun getAllProjects(): Result<List<Project>> {
        return projectDataSource.read().fold(
            onFailure = { error -> Result.failure(PlanMateExceptions.DataException.ReadException()) },
            onSuccess = { projectsList -> Result.success(projectsList) }
        )
    }
}