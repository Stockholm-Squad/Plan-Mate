package org.example.data.datasources.state_data_source

import org.example.data.models.ProjectStateModel

interface IStateDataSource {
    fun read(): Result<List<ProjectStateModel>>
    fun overWrite(users: List<ProjectStateModel>): Result<Boolean>
    fun append(users: List<ProjectStateModel>): Result<Boolean>
}