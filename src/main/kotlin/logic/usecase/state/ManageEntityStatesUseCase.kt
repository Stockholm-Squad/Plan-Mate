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
            entityStateRepository.isEntityStateExist(validStateName)
                .takeUnless { isEntityStateExist -> isEntityStateExist }
                ?.let {
                    entityStateRepository.addEntityState(EntityState(title = validStateName))
                } ?: throw EntityStateAlreadyExistException()
        }
    }

    suspend fun updateEntityStateByName(stateName: String, newStateName: String): Boolean {
        return isStateNameValid(stateName).let { validStateName ->
            isStateNameValid(newStateName).let { validNewStateName ->
                entityStateRepository.getEntityStateByName(validStateName).let { state ->
                    entityStateRepository.updateEntityState(EntityState(state.id, validNewStateName))
                }
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
        return isStateNameValid(stateName).let { validStateName ->
            entityStateRepository.getEntityStateByName(validStateName).id
        }
    }

    suspend fun getEntityStateNameByStateId(stateId: UUID): String {
        return entityStateRepository.getEntityStateByID(stateId).title
    }

    private fun isStateNameValid(stateName: String): String {
        return stateName.trim().takeIf {
            it.isNotBlank() &&
                    it.isValidLength(30) &&
                    it.isLetterOrWhiteSpace()
        } ?: throw NotAllowedEntityStateNameException()
    }
}