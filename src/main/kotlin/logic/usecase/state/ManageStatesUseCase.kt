package org.example.logic.usecase.state

import logic.model.entities.ProjectState
import org.example.logic.model.exceptions.*
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.extention.isLetterOrWhiteSpace
import org.example.logic.usecase.extention.isValidLength
import java.util.*

class ManageStatesUseCase(
    private val projectStateRepository: ProjectStateRepository,
) {
    fun addProjectState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                validStateName.takeIf { state -> !isProjectStateExist(state) }
                    ?.let {
                        projectStateRepository.addProjectState(it).fold(
                            onSuccess = { Result.success(true) },
                            onFailure = { exception -> Result.failure(exception) }
                        )

                    } ?: Result.failure(StateAlreadyExistException())
            },
            onFailure = { Result.failure(NotAllowedStateNameException()) }
        )
    }


    fun editProjectStateByName(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = { validStateName ->
                getProjectState(validStateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        projectStateRepository.editProjectState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable ->
                                this@ManageStatesUseCase.handleProjectStateNotExistException(
                                    throwable
                                )
                            }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }


    private fun isStateNameValid(stateName: String): Result<String> {
        return stateName.trim().takeIf {
            it.isNotBlank() &&
                    it.isValidLength(30) &&
                    it.isLetterOrWhiteSpace()
        }?.let { Result.success(it) }
            ?: Result.failure(NotAllowedStateNameException())
    }

    fun deleteProjectState(stateName: String): Result<Boolean> {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getProjectState(stateName)
                    .takeIf { it != null }
                    ?.let { state ->
                        projectStateRepository.deleteProjectState(state).fold(
                            onSuccess = { Result.success(it) },
                            onFailure = { throwable ->
                                this@ManageStatesUseCase.handleProjectStateNotExistException(
                                    throwable
                                )
                            }
                        )
                    } ?: Result.failure(StateNotExistException())
            },
            onFailure = { throwable -> Result.failure(throwable) }
        )
    }

    fun getAllProjectStates(): Result<List<ProjectState>> {
        return projectStateRepository.getAllProjectStates().fold(
            onSuccess = { data ->
                data.takeIf { data.isNotEmpty() }?.let {
                    Result.success(data)
                } ?: Result.failure(EmptyDataException())
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    fun getProjectStateIdByName(stateName: String): UUID? {
        return isStateNameValid(stateName).fold(
            onSuccess = {
                getProjectState(stateName).takeIf { it != null }?.id
            },
            onFailure = { null }
        )
    }

    private fun handleProjectStateNotExistException(throwable: Throwable): Result<Boolean> {
        return throwable.takeIf { it is FileNotExistException }
            ?.let { Result.failure(StateNotExistException()) }
            ?: Result.failure(throwable)
    }


    private fun isProjectStateExist(stateName: String): Boolean {
        return (getProjectState(stateName) != null)
    }

    private fun getProjectState(stateName: String): ProjectState? {
        return this.getAllProjectStates().fold(
            onSuccess = { allStates ->
                allStates.firstOrNull { state -> state.name.equals(stateName, ignoreCase = true) }
            },
            onFailure = { null }
        )
    }
    fun getProjectStateNameByStateId(stateId: UUID): ProjectState? {
        return getAllProjectStates()
            .fold(
                onSuccess = { states -> findStateById(states, stateId) },
                onFailure = { null }
            )
    }

    private fun findStateById(states: List<ProjectState>, stateId: UUID): ProjectState? {
        return states.firstOrNull { it.id == stateId }
            ?.let { throw StateNotExistException()}
    }


}