package org.example.data.datasources.state_data_source

import org.example.data.models.ProjectStateModel

interface IStateDataSource {
    suspend fun read(): List<ProjectStateModel>
    suspend fun overWrite(users: List<ProjectStateModel>): Boolean
    suspend fun append(users: List<ProjectStateModel>): Boolean
}