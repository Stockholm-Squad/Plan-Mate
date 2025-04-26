package org.example.data.datasources

import org.example.logic.entities.Project

interface ProjectDataSource {
    fun getProjectById(id: String): Result<Project>
    fun addProject(project: Project): Result<Boolean>
    fun editProject(project: Project): Result<Boolean>
    fun deleteProject(id: String): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
}