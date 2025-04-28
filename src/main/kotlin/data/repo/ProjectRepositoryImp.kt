package org.example.data.repo

import logic.model.entities.Project
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: PlanMateDataSource<Project>
) : ProjectRepository {

    override fun getProjectById(id: String): Result<Project> {
        TODO()
    }

    override fun addProject(project: Project): Result<Boolean> {
        TODO()
    }

    override fun editProject(project: Project): Result<Boolean> {
        TODO()
    }

    override fun deleteProject(id: String): Result<Boolean> {
        TODO()
    }

    override fun getAllProjects(): Result<List<Project>> {
        TODO()
    }
}