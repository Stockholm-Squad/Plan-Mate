package org.example.logic.usecase.state

import logic.model.entities.State
import org.example.logic.model.exceptions.*
import org.example.logic.repository.StateRepository

class ManageStatesUseCase(
    private val stateRepository: StateRepository,
) {
    fun addState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                validStateName.takeIf { state -> !isStateExist(state) }
                    ?.let {
                        stateRepository.addState(it).fold(
                            onSuccess = { Result.success(true) },
                            onFailure = { exception -> Result.failure(exception) }
                        )

                    } ?: Result.failure(StateAlreadyExistException())
            },
            onFailure = { Result.failure(NotAllowedStateNameException()) }
        )
    }


    fun editState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                getState(validStateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        stateRepository.editState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }


    private fun isStateNameValid(stateName: String): Result<String> {
        return stateName.trim().takeIf {
            it.isNotBlank() &&
                    isValidLength(it) &&
                    isLetterAndWhiteSpace(it)
        }?.let { Result.success(it) }
            ?: Result.failure(NotAllowedStateNameException())
    }

    fun deleteState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getState(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        stateRepository.deleteState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable -> this@ManageStatesUseCase.handleStateNotExistException(throwable) }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    fun getAllStates(): Result<List<State>> {
        return stateRepository.getAllStates().fold(
            onSuccess = { data ->
                data.takeIf { data.isNotEmpty() }?.let {
                    Result.success(data)
                } ?: Result.failure(EmptyDataException())
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    fun getStateIdByName(stateName: String): String? {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getState(stateName).takeIf { it != null }?.id
            },
            onFailure = { null }
        )
    }

    private fun handleStateNotExistException(throwable: Throwable): Result<Boolean> {
        return throwable.takeIf { it is FileNotExistException }
            ?.let { Result.failure(StateNotExistException()) }
            ?: Result.failure(throwable)
    }


    private fun isStateExist(stateName: String): Boolean {
        return (getState(stateName) != null)
    }

    private fun getState(stateName: String): State? {
        return this.getAllStates().fold(
            onSuccess = { allStates ->
                allStates.firstOrNull { state -> state.name.equals(stateName, ignoreCase = true) }
            },
            onFailure = { null }
        )
    }


    private fun isValidLength(stateName: String): Boolean {
        return stateName.length <= 30
    }

    private fun isLetterAndWhiteSpace(stateName: String): Boolean {
        return stateName.all { char -> char.isLetter() || char.isWhitespace() }
    }

}