package org.example.data.datasources.state

import logic.model.entities.State

class StateCsvDataSource : StateDataSource {
    override fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editState(state: State): Result<Boolean> {
        return Result.success(false)
    }

    override fun deleteState(id: String): Result<Boolean> {
        return Result.success(false)
    }

    override fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }
}