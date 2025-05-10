package org.example.logic.usecase.state

import org.example.logic.NotAllowedStateNameException
import org.example.logic.StateAlreadyExistException
import org.example.logic.entities.EntityState
import org.example.logic.repository.EntityStateRepository
import org.example.logic.utils.isLetterOrWhiteSpace
import org.example.logic.utils.isValidLength
import java.util.*

class ManageEntityStatesUseCase(
    private val entityEntityStateRepository: EntityStateRepository,
) {
    suspend fun addEntityState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            validStateName.takeIf { state ->
                !entityEntityStateRepository.isEntityStateExist(state)
            }
        }
            ?.let {
                entityEntityStateRepository.addEntityState(EntityState(name = it))
            } ?: throw StateAlreadyExistException()
    }


    suspend fun editEntityStateByName(stateName: String, newStateName: String): Boolean {
        return validateStateNames(stateName, newStateName).let { (validatedStateName, validatedNewStateName) ->
            entityEntityStateRepository.getEntityStateByName(validatedStateName).let { state ->
                entityEntityStateRepository.editEntityState(EntityState(state.id, validatedNewStateName))
            }
        }
    }

    suspend fun deleteEntityState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            entityEntityStateRepository.getEntityStateByName(validStateName).let { state ->
                entityEntityStateRepository.deleteEntityState(state)
            }
        }
    }

    suspend fun getAllEntityStates(): List<EntityState> {
        return entityEntityStateRepository.getAllEntityStates()
    }

    suspend fun getEntityStateIdByName(stateName: String): UUID {
        return isStateNameValid(stateName).let {
            entityEntityStateRepository.getEntityStateByName(stateName).id
        }
    }

    suspend fun getEntityStateNameByStateId(stateId: UUID): String {
        return entityEntityStateRepository.getEntityStateByID(stateId).name
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
        } ?: throw NotAllowedStateNameException()
    }
}