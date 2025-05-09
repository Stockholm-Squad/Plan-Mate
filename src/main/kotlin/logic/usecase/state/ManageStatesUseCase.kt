package org.example.logic.usecase.state

import logic.models.entities.ProjectState
import logic.models.exceptions.StateExceptions
import org.example.logic.repository.ProjectStateRepository
import org.example.logic.usecase.extention.isLetterOrWhiteSpace
import org.example.logic.usecase.extention.isValidLength
import java.util.*

class ManageStatesUseCase(
    private val projectStateRepository: ProjectStateRepository,
) {
    suspend fun addProjectState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            validStateName.takeIf { state ->
                !projectStateRepository.isProjectStateExist(state)
            }
        }
            ?.let {
                projectStateRepository.addProjectState(ProjectState(name = it))
            } ?: throw StateExceptions.StateAlreadyExistException()
    }


    suspend fun editProjectStateByName(stateName: String, newStateName: String): Boolean {
        return validateStateNames(stateName, newStateName).let { (validatedStateName, validatedNewStateName) ->
            projectStateRepository.getProjectStateByName(validatedStateName).let { state ->
                projectStateRepository.editProjectState(ProjectState(state.id, validatedNewStateName))
            }
        }
    }

    suspend fun deleteProjectState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            projectStateRepository.getProjectStateByName(validStateName).let { state ->
                projectStateRepository.deleteProjectState(state)
            }
        }
    }

    suspend fun getAllProjectStates(): List<ProjectState> {
        return projectStateRepository.getAllProjectStates()
    }

    suspend fun getProjectStateIdByName(stateName: String): UUID {
        return isStateNameValid(stateName).let {
            projectStateRepository.getProjectStateByName(stateName).id
        }
    }

    suspend fun getProjectStateNameByStateId(stateId: UUID): String {
        return projectStateRepository.getProjectStateByID(stateId).name
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
}