package org.example.data.datasources

import data.dto.ProjectStateModel

interface IStateDataSource {
    suspend fun read(): List<ProjectStateModel>
    suspend fun overWrite(state: List<ProjectStateModel>): Boolean
    suspend fun append(state: List<ProjectStateModel>): Boolean
}