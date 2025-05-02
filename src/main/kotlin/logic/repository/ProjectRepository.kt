package org.example.logic.repository

import logic.model.entities.Project
import logic.model.entities.User

interface ProjectRepository {

    fun addProject(project: Project): Result<Boolean>
    fun editProject(updatedProject: Project): Result<Boolean>
    fun deleteProject(projectToDelete: Project): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>

    fun getUsersAssignedToProject(projectId: String): Result<List<User>>
    fun addUserAssignedToProject(projectId: String, userName: String): Result<Boolean>
    fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean>
}