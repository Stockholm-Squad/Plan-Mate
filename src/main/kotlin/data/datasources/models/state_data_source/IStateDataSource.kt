package org.example.data.datasources.models.state_data_source

import org.example.data.models.State

interface IStateDataSource {
    fun read(): Result<List<State>>
    fun overWrite(users: List<State>): Result<Boolean>
    fun append(users: List<State>): Result<Boolean>
}