package org.example.logic.usecase.state

import logic.model.entities.State
import org.example.logic.repository.StateRepository

class ManageStatesUseCase(private val stateRepository: StateRepository) {
    fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun editState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun deleteState(id: String): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }
}