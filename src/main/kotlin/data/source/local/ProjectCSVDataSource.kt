package org.example.data.source.local

import data.dto.ProjectDto
import org.example.data.source.ProjectDataSource

class ProjectCSVDataSource : ProjectDataSource {
    override suspend fun addProject(project: ProjectDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateProject(updatedProject: ProjectDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProject(projectToDelete: ProjectDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProjects(): List<ProjectDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectsByUsername(username: String): List<ProjectDto> {
        TODO("Not yet implemented")
    }
}