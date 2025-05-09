package org.example.data.datasources.state_data_source

import org.example.data.models.ProjectStateModel

interface StateDataSource {
    suspend fun addProjectState(projectState: ProjectStateModel): Boolean
    suspend fun editProjectState(projectState: ProjectStateModel): Boolean
    suspend fun deleteProjectState(projectState: ProjectStateModel): Boolean
    suspend fun isProjectStateExist(stateName: String): Boolean
    suspend fun getAllProjectStates(): List<ProjectStateModel>
    suspend fun getProjectStateByName(stateName: String): ProjectStateModel?
    suspend fun getProjectStateById(stateId: String): ProjectStateModel?
}