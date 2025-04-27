package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.state.StateDataSource
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: StateDataSource
) : StateRepository {

    override fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteState(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }
}