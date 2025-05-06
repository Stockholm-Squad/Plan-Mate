package org.example.logic.repository

import logic.models.entities.ProjectState

interface ProjectStateRepository {
    suspend fun addProjectState(stateName: String): Boolean
    suspend fun editProjectState(projectState: ProjectState): Boolean
    suspend fun deleteProjectState(projectState: ProjectState): Boolean
    suspend fun getAllProjectStates(): List<ProjectState>
}