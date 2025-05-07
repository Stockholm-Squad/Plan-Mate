package org.example.data.datasources.state_data_source

import org.example.data.models.ProjectStateModel

interface IStateDataSource {
    suspend fun read(): List<ProjectStateModel>
    suspend fun overWrite(state: List<ProjectStateModel>): Boolean
    suspend fun append(state: List<ProjectStateModel>): Boolean
}