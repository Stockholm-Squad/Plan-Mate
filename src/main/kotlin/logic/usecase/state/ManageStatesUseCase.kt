package org.example.logic.usecase.state

import kotlinx.coroutines.runBlocking
import logic.models.entities.ProjectState
import logic.models.exceptions.*
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.extention.isLetterOrWhiteSpace
import org.example.logic.usecase.extention.isValidLength
import java.util.*

class ManageStatesUseCase(
    private val projectStateRepository: ProjectStateRepository,
) {
    fun addProjectState(stateName: String): Boolean {
        val validStateName = isStateNameValid(stateName)
        return validStateName.takeIf { state ->
            runBlocking {
                !isProjectStateExist(state)
            }
        }
            ?.let {
                runBlocking {
                    projectStateRepository.addProjectState(it)
                }
            } ?: throw StateExceptions.StateAlreadyExistException()
    }


    fun editProjectStateByName(stateName: String, newStateName: String): Boolean {
        return validateStateNames(stateName, newStateName).let {
            runBlocking {
                getProjectState(stateName).let { state ->
                    projectStateRepository.editProjectState(ProjectState(state.id, newStateName))
                }
            }
        }
    }

    fun deleteProjectState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            runBlocking {
                getProjectState(validStateName).let { state ->
                    projectStateRepository.deleteProjectState(state)
                }
            }
        }
    }

    fun getAllProjectStates(): List<ProjectState> {
        return runBlocking {
            projectStateRepository.getAllProjectStates().also { data ->
                data.takeIf { data.isNotEmpty() }?.let {
                    Result.success(data)
                } ?: Result.failure(DataException.EmptyDataException())
            }
        }
    }

    fun getProjectStateIdByName(stateName: String): UUID {
        return isStateNameValid(stateName).let {
            runBlocking {
                getProjectState(stateName).id
            }
        }
    }

    fun getProjectStateNameByStateId(stateId: UUID): String? {
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

    private fun isProjectStateExist(stateName: String): Boolean {
        return (getProjectState(stateName) != null)
    }

    private fun getProjectState(stateName: String): ProjectState {
        return this.getAllProjectStates().let { allStates ->
            allStates.firstOrNull { state -> state.name.equals(stateName, ignoreCase = true) }
                ?: throw StateExceptions.StateNotExistException()
        }
    }

    private fun findStateById(states: List<ProjectState>, stateId: UUID): ProjectState? {
        return states.firstOrNull { it.id == stateId }
    }
}