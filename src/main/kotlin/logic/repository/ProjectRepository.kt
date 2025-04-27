package org.example.logic.repository

import logic.model.entities.Project

interface ProjectRepository {
    fun getProjectById(id: String): Result<Project>
    fun addProject(project: Project): Result<Boolean>
    fun editProject(project: Project): Result<Boolean>
    fun deleteProject(id: String): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
}