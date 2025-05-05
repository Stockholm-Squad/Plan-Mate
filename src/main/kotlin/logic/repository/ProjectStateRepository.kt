package org.example.logic.repository

import logic.models.entities.ProjectState

interface ProjectStateRepository {
    fun addProjectState(stateName: String): Result<Boolean>
    fun editProjectState(projectState: ProjectState): Result<Boolean>
    fun deleteProjectState(projectState: ProjectState): Result<Boolean>
    fun getAllProjectStates(): Result<List<ProjectState>>
}