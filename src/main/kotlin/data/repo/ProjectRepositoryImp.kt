package org.example.data.repo

import org.example.data.datasources.ProjectDataSource
import org.example.logic.entities.Project
import org.example.logic.repository.ProjectRepository

class ProjectRepositoryImp(
    private val projectDataSource: ProjectDataSource
) : ProjectRepository {

    override fun getProjectById(id: String): Result<Project> {
        TODO("Not yet implemented")
    }

    override fun addProject(project: Project): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editProject(project: Project): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteProject(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): Result<List<Project>> {
        TODO("Not yet implemented")
    }
}