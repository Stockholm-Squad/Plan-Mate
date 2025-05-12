package org.example.data.source

import data.dto.EntityStateDto

interface EntityStateDataSource {
    suspend fun addEntityState(entityState: EntityStateDto): Boolean
    suspend fun updatedState(entityState: EntityStateDto): Boolean
    suspend fun deleteEntityState(entityState: EntityStateDto): Boolean
    suspend fun isEntityStateExist(stateName: String): Boolean
    suspend fun getAllEntityStates(): List<EntityStateDto>
    suspend fun getEntityStateByName(stateName: String): EntityStateDto?
    suspend fun getEntityStateById(stateId: String): EntityStateDto?
}