package org.example.logic.repository

import logic.model.entities.Project
import logic.model.entities.User

interface ProjectRepository {

    fun addProject(project: Project): Result<Boolean>
    fun editProject(updatedProject: Project): Result<Boolean>
    fun deleteProject(projectToDelete: Project): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
    fun getProjectsAssignedToUser(userName: String): Result<List<Project>>
}