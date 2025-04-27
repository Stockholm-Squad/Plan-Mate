package org.example.data.datasources.project

import logic.model.entities.Project

interface ProjectDataSource {
    fun getProjectById(id: String): Result<Project>
    fun addProject(project: Project): Result<Boolean>
    fun editProject(project: Project): Result<Boolean>
    fun deleteProject(id: String): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
}