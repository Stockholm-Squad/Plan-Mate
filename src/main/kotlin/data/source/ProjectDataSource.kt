package org.example.data.source

import data.dto.ProjectDto
import data.dto.UserAssignedToProjectDto

interface ProjectDataSource {
    suspend fun addProject(project: ProjectDto): Boolean
    suspend fun updateProject(projectToUpdate: ProjectDto): Boolean
    suspend fun deleteProject(projectToDelete: ProjectDto): Boolean
    suspend fun getAllProjects(): List<ProjectDto>
    suspend fun getProjectsByUsername(username: String): List<ProjectDto>

    suspend fun getUsersAssignedToProjectByUserName(userName: String): List<UserAssignedToProjectDto>
}