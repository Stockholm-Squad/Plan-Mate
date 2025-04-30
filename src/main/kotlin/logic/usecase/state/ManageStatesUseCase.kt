package org.example.logic.usecase.state

import logic.model.entities.State
import org.example.logic.repository.StateRepository

class ManageStatesUseCase(
    private val stateRepository: StateRepository
) {
    fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun editState(stateName: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun deleteState(stateName: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }

    private fun isStateExist(stateName: String): Result<Boolean>{
        TODO("Not yet implemented")
    }
}