package org.example.data.source.local

import data.dto.EntityStateDto
import org.example.data.source.EntityStateDataSource

class EntityStateCSVDataSource : EntityStateDataSource {
    override suspend fun addEntityState(entityState: EntityStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateEntityState(entityState: EntityStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteEntityState(entityState: EntityStateDto): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isEntityStateExist(stateName: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEntityStates(): List<EntityStateDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getEntityStateByName(stateName: String): EntityStateDto? {
        TODO("Not yet implemented")
    }

    override suspend fun getEntityStateById(stateId: String): EntityStateDto? {
        TODO("Not yet implemented")
    }
}