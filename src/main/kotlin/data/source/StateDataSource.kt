package org.example.data.source

import data.dto.ProjectStateDto

interface StateDataSource {
    suspend fun addProjectState(projectState: ProjectStateDto): Boolean
    suspend fun editProjectState(projectState: ProjectStateDto): Boolean
    suspend fun deleteProjectState(projectState: ProjectStateDto): Boolean
    suspend fun isProjectStateExist(stateName: String): Boolean
    suspend fun getAllProjectStates(): List<ProjectStateDto>
    suspend fun getProjectStateByName(stateName: String): ProjectStateDto?
    suspend fun getProjectStateById(stateId: String): ProjectStateDto?
}