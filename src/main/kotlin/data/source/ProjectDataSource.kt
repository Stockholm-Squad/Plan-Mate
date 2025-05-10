package org.example.data.source

import data.dto.ProjectModel

interface ProjectDataSource {
    suspend fun addProject(project: ProjectModel): Boolean
    suspend fun editProject(updatedProject: ProjectModel): Boolean
    suspend fun deleteProject(projectToDelete: ProjectModel): Boolean
    suspend fun getAllProjects(): List<ProjectModel>
    suspend fun getProjectsByUsername(username: String): List<ProjectModel>
}