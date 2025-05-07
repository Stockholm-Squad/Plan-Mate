package org.example.logic.usecase.state

import logic.models.entities.ProjectState
import logic.models.exceptions.*
import org.example.data.utils.executeSafelyWithContext
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.extention.isLetterOrWhiteSpace
import org.example.logic.usecase.extention.isValidLength
import java.util.*

class ManageStatesUseCase(
    private val projectStateRepository: ProjectStateRepository,
) {
    suspend fun addProjectState(stateName: String): Boolean {
        val validStateName = isStateNameValid(stateName)
        return validStateName.takeIf { state ->
            !isProjectStateExist(state)
        }
            ?.let {
                projectStateRepository.addProjectState(it)
            } ?: throw StateExceptions.StateAlreadyExistException()
    }


    suspend fun editProjectStateByName(stateName: String, newStateName: String): Boolean {
        return validateStateNames(stateName, newStateName).let {
            getProjectState(stateName).let { state ->
                projectStateRepository.editProjectState(ProjectState(state.id, newStateName))
            }
        }
    }

    suspend fun deleteProjectState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            getProjectState(validStateName).let { state ->
                projectStateRepository.deleteProjectState(state)
            }
        }
    }

    suspend fun getAllProjectStates(): List<ProjectState> {
        return projectStateRepository.getAllProjectStates().also { data ->
            data.takeIf { data.isNotEmpty() }?.let {
                Result.success(data)
            } ?: Result.failure(DataException.EmptyDataException())

        }
    }

    suspend fun getProjectStateIdByName(stateName: String): UUID {
        return isStateNameValid(stateName).let {
            getProjectState(stateName).id
        }
    }

    suspend fun getProjectStateNameByStateId(stateId: UUID): String? {
        return getAllProjectStates()
            .let { states -> findStateById(states, stateId)?.name }
    }

    private fun validateStateNames(stateName: String, newStateName: String): Pair<String, String> {
        return isStateNameValid(stateName).let { validStateName ->
            isStateNameValid(newStateName).let { validNewName ->
                validStateName to validNewName
            }
        }
    }

    private fun isStateNameValid(stateName: String): String {
        return stateName.trim().takeIf {
            it.isNotBlank() &&
                    it.isValidLength(30) &&
                    it.isLetterOrWhiteSpace()
        } ?: throw StateExceptions.NotAllowedStateNameException()
    }

    private suspend fun isProjectStateExist(stateName: String): Boolean {
        return executeSafelyWithContext(
            onSuccess = {
                getProjectState(stateName)
                true
            },
            onFailure = { false }
        )
    }

    private suspend fun getProjectState(stateName: String): ProjectState {
        return this.getAllProjectStates().let { allStates ->
            allStates.firstOrNull { state -> state.name.equals(stateName, ignoreCase = true) }
                ?: throw StateExceptions.StateNotExistException()
        }
    }

    private fun findStateById(states: List<ProjectState>, stateId: UUID): ProjectState? {
        return states.firstOrNull { it.id == stateId }
    }
}