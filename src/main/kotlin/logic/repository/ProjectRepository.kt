package org.example.logic.repository

import logic.model.entities.Project

interface ProjectRepository {

    fun addProject(project: Project): Result<Boolean>
    fun editProject(updatedProject: Project): Result<Boolean>
    fun deleteProject(projectToDelete: Project): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
    fun getProjectsByUsername(username: String): Result<List<Project>>
}