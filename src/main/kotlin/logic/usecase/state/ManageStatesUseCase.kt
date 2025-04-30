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
        return isStateNameValid(stateName).fold(
            onSuccess = {
                isStateExist(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        stateRepository.editState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                        )
                    } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    private fun isStateNameValid(stateName: String): Result<Boolean> {
        return stateName.trim().takeIf {
            stateName.isNotBlank() &&
                    stateName.length <= 20 &&
                    stateName.contains("^[A-Za-z]+\$")
        }?.let { Result.success(true) }
            ?: Result.failure(PlanMateExceptions.LogicException.InvalidStateName())
    }

    fun deleteState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                isStateExist(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        stateRepository.deleteState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                        )
                    } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    private fun handleStateNotExistException(throwable: Throwable): Result<Boolean> {
        return throwable.takeIf { it is PlanMateExceptions.DataException.FileNotExistException }
            ?.let { Result.failure(PlanMateExceptions.LogicException.StateNotExistException()) }
            ?: Result.failure(throwable)
    }

    fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }

    fun getStateIdByName(stateName: String): String? {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                isStateExist(stateName).takeIf { it != null }?.id
            },
            onFailure = { null }
        )
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