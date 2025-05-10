package org.example.data.source.local

import data.dto.ProjectStateDto
import org.example.data.source.StateDataSource

class StateCSVDataSource : StateDataSource {
    override suspend fun addProjectState(projectState: ProjectStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun editProjectState(projectState: ProjectStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProjectState(projectState: ProjectStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isProjectStateExist(stateName: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllProjectStates(): List<ProjectStateDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectStateByName(stateName: String): ProjectStateDto? {
        TODO("Not yet implemented")
    }

    override suspend fun getProjectStateById(stateId: String): ProjectStateDto? {
        TODO("Not yet implemented")
    }
}