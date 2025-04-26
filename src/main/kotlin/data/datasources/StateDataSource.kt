package org.example.data.datasources

import org.example.logic.entities.State

interface StateDataSource {
    fun addState(state: State): Result<Boolean>
    fun editState(state: State): Result<Boolean>
    fun deleteState(id: String): Result<Boolean>
    fun getAllStates(): Result<List<State>>
}