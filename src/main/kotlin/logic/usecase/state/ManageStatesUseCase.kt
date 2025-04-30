package org.example.logic.usecase.state

import logic.model.entities.State
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class ManageStatesUseCase(
    private val stateRepository: StateRepository
) {
    fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    fun editState(stateName: String): Result<Boolean> {
        return isStateExist(stateName)
            .takeIf { it != null }
            ?.let { state ->
                stateRepository.editState(state).fold(
                    onSuccess = { Result.success(it) },
                    onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                )
            } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
    }

    fun deleteState(stateName: String): Result<Boolean> {
        return isStateExist(stateName)
            .takeIf { it != null }
            ?.let { state ->
                stateRepository.deleteState(state).fold(
                    onSuccess = { Result.success(it) },
                    onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                )
            } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
    }

    private fun handleStateNotExistException(throwable: Throwable): Result<Boolean> {
        return throwable.takeIf { it is PlanMateExceptions.DataException.FileNotExistException }
            ?.let { Result.failure(PlanMateExceptions.LogicException.StateNotExistException()) }
            ?: Result.failure(throwable)
    }

    fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }

    private fun isStateExist(stateName: String): State? {
        return this.getAllStates().fold(
            onSuccess = { allStates ->
                allStates.firstOrNull { state -> state.name == stateName }
            },
            onFailure = { null }
        )
    }
}