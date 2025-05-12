package org.example.logic.repository

import org.example.logic.entities.EntityState
import java.util.*

interface EntityStateRepository {
    suspend fun addEntityState(entityState: EntityState): Boolean
    suspend fun updateEntityState(entityState: EntityState): Boolean
    suspend fun deleteEntityState(entityState: EntityState): Boolean
    suspend fun isEntityStateExist(stateName: String): Boolean
    suspend fun getAllEntityStates(): List<EntityState>
    suspend fun getEntityStateByName(stateName: String): EntityState
    suspend fun getEntityStateByID(stateId: UUID): EntityState
}