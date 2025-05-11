package org.example.logic.repository

import org.example.logic.entities.Project

interface ProjectRepository {
    suspend fun addProject(project: Project): Boolean
    suspend fun UpdatedProject(updatedProject: Project): Boolean
    suspend fun deleteProject(projectToDelete: Project): Boolean
    suspend fun getAllProjects(): List<Project>
    suspend fun getProjectsByUsername(username: String): List<Project>
}