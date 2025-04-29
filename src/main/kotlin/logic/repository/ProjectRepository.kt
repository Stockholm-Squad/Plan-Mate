package org.example.logic.repository

import logic.model.entities.Project

interface ProjectRepository {

    fun addProject(project: Project): Result<Boolean>
    fun editProject(project: Project): Result<Boolean>
    fun deleteProject(project: Project): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>
}