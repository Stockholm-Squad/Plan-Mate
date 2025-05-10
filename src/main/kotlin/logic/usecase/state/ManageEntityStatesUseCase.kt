package org.example.logic.usecase.state

import org.example.logic.EntityStateAlreadyExistException
import org.example.logic.NotAllowedEntityStateNameException
import org.example.logic.entities.EntityState
import org.example.logic.repository.EntityStateRepository
import org.example.logic.utils.isLetterOrWhiteSpace
import org.example.logic.utils.isValidLength
import java.util.*

class ManageEntityStatesUseCase(
    private val entityStateRepository: EntityStateRepository,
) {
    suspend fun addEntityState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            validStateName.takeIf { state ->
                !entityStateRepository.isEntityStateExist(state)
            }
        }
            ?.let {
                entityStateRepository.addEntityState(EntityState(name = it))
            } ?: throw EntityStateAlreadyExistException()
    }


    suspend fun editEntityStateByName(stateName: String, newStateName: String): Boolean {
        return validateStateNames(stateName, newStateName).let { (validatedStateName, validatedNewStateName) ->
            entityStateRepository.getEntityStateByName(validatedStateName).let { state ->
                entityStateRepository.editEntityState(EntityState(state.id, validatedNewStateName))
            }
        }
    }

    suspend fun deleteEntityState(stateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            entityStateRepository.getEntityStateByName(validStateName).let { state ->
                entityStateRepository.deleteEntityState(state)
            }
        }
    }

    suspend fun getAllEntityStates(): List<EntityState> {
        return entityStateRepository.getAllEntityStates()
    }

    suspend fun getEntityStateIdByName(stateName: String): UUID {
        return isStateNameValid(stateName).let {
            entityStateRepository.getEntityStateByName(stateName).id
        }
    }

    suspend fun getEntityStateNameByStateId(stateId: UUID): String {
        return entityStateRepository.getEntityStateByID(stateId).name
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
        } ?: throw NotAllowedEntityStateNameException()
    }
}