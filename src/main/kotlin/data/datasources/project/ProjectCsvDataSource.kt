package org.example.data.datasources.project

import logic.model.entities.Project

class ProjectCsvDataSource : ProjectDataSource {
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