package org.example.logic.repository

import logic.model.entities.ProjectState

interface ProjectStateRepository {
    fun addProjectState(projectState: ProjectState): Result<Boolean>
    fun editProjectState(projectState: ProjectState): Result<Boolean>
    fun deleteProjectState(projectState: ProjectState): Result<Boolean>
    fun getAllProjectStates(): Result<List<ProjectState>>
}