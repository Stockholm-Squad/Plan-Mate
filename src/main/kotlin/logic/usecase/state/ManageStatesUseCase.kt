package org.example.logic.usecase.state

import logic.model.entities.State
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class ManageStatesUseCase(
    private val stateRepository: StateRepository,
) {
    fun addState(stateName: String): Result<Boolean> {
        return stateRepository.addState(stateName).fold(
            onSuccess = {
                stateName.trim()
                it.let {
                    stateName.isNotBlank() && isValidLength(stateName) && !isStateExists(stateName) && isLetterAndWhiteSpace(stateName)
                }
                Result.success(
                    true
                )
            },
            onFailure = { Result.failure(PlanMateExceptions.LogicException.NotAllowedStateNameException()) }
        )

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
        return stateName.takeIf {
            stateName.isNotBlank() &&
                    stateName.length <= 20 &&
                    stateName.contains("^[A-Za-z]+$".toRegex()) // Ensure only letters
        }?.let { Result.success(true) }
            ?: Result.failure(PlanMateExceptions.LogicException.NotAllowedStateNameException()) // Invalid state name
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
        return stateRepository.getAllStates().fold(
            onSuccess = { data ->
                data.takeIf { data.isNotEmpty() }?.let {
                    Result.success(data)
                } ?: Result.failure(PlanMateExceptions.DataException.EmptyDataException())
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.ReadException()) }
        )
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


    private fun isValidLength(stateName: String): Boolean {
        return stateName.length <= 30
    }

    private fun isLetterAndWhiteSpace(stateName: String): Boolean {
        return stateName.all { char -> char.isLetter() || char.isWhitespace() }
    }

    private fun isStateExists(stateName: String): Boolean {

        return getAllStates().getOrThrow().any { state: State -> state.name.equals(stateName, ignoreCase = true) }
    }
}