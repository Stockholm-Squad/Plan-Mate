package org.example.logic.repository

import org.example.logic.entities.Project
import java.util.*

interface ProjectRepository {
    suspend fun addProject(project: Project): Boolean
    suspend fun updateProject(projectToUpdate: Project): Boolean
    suspend fun deleteProject(projectToDelete: Project): Boolean
    suspend fun getAllProjects(): List<Project>
    suspend fun getProjectsByUsername(username: String): List<Project>
    suspend fun getProjectByName(projectName: String): Project
    suspend fun getProjectById(projectId: UUID): Project
}