package org.example.data.source

import data.dto.ProjectDto

interface ProjectDataSource {
    suspend fun addProject(project: ProjectDto): Boolean
    suspend fun updateProject(projectToUpdate: ProjectDto): Boolean
    suspend fun deleteProject(projectToDelete: ProjectDto): Boolean
    suspend fun getAllProjects(): List<ProjectDto>
    suspend fun getProjectsByUsername(username: String): List<ProjectDto>
}