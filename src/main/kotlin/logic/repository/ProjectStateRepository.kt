package org.example.logic.repository

import logic.models.entities.ProjectState
import java.util.UUID

interface ProjectStateRepository {
    suspend fun addProjectState(projectState: ProjectState): Boolean
    suspend fun editProjectState(projectState: ProjectState): Boolean
    suspend fun deleteProjectState(projectState: ProjectState): Boolean
    suspend fun isProjectStateExist(stateName: String): Boolean
    suspend fun getAllProjectStates(): List<ProjectState>
    suspend fun getProjectStateByName(stateName: String): ProjectState
    suspend fun getProjectStateByID(stateId: UUID): ProjectState
}