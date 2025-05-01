package org.example.logic.repository

import logic.model.entities.Project

interface ProjectRepository {

    fun addProject(project: Project): Result<Boolean>
    fun editProject(project: Project): Result<Boolean>
    fun deleteProject(project: Project): Result<Boolean>
    fun getAllProjects(): Result<List<Project>>

    fun getTasksInProject(projectId: String): Result<List<String>>
    fun addTaskInProject(projectId: String, taskId: String): Result<Boolean>
    fun deleteTaskFromProject(projectId: String, taskId: String): Result<Boolean>

    fun getUsersAssignedToProject(projectId: String): Result<List<String>>
    fun addUserAssignedToProject(projectId: String, userName: String): Result<Boolean>
    fun deleteUserAssignedToProject(projectId: String, userName: String): Result<Boolean>
}