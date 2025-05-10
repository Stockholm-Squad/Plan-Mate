package org.example.data.repo

import org.example.data.mapper.mapToStateEntity
import org.example.data.mapper.mapToStateModel
import org.example.data.source.EntityStateDataSource
import org.example.data.utils.tryToExecute
import org.example.logic.*
import org.example.logic.entities.EntityState
import org.example.logic.repository.EntityStateRepository
import java.util.*

class EntityStateRepositoryImp(
    private val entityStateDataSource: EntityStateDataSource,
) : EntityStateRepository {

    override suspend fun addEntityState(entityState: EntityState): Boolean {
        return tryToExecute(
            { entityStateDataSource.addEntityState(entityState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw EntityEntityStateNotAddedException() }
        )
    }

    override suspend fun editEntityState(entityState: EntityState): Boolean {
        return tryToExecute(
            { entityStateDataSource.editEntityState(entityState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw EntityEntityStateNotEditedException() }
        )
    }

    override suspend fun deleteEntityState(entityState: EntityState): Boolean {
        return tryToExecute(
            { entityStateDataSource.deleteEntityState(entityState.mapToStateModel()) },
            onSuccess = { it },
            onFailure = { throw EntityEntityStateNotDeletedException() }
        )

    }

    override suspend fun isEntityStateExist(stateName: String): Boolean {
        return tryToExecute(
            { entityStateDataSource.isEntityStateExist(stateName) },
            onSuccess = { it },
            onFailure = { throw NoEntityEntityStateFoundException() }
        )
    }

    override suspend fun getAllEntityStates(): List<EntityState> {
        return tryToExecute(
            { entityStateDataSource.getAllEntityStates() },
            onSuccess = { it },
            onFailure = { throw NoEntityStatesFoundedException() }
        ).mapNotNull { it.mapToStateEntity() }
    }

    override suspend fun getEntityStateByName(stateName: String): EntityState {
        return tryToExecute(
            { entityStateDataSource.getEntityStateByName(stateName) },
            onSuccess = { it },
            onFailure = { throw NoEntityEntityStateFoundException() }
        )?.mapToStateEntity() ?: throw NoEntityEntityStateFoundException()
    }

    override suspend fun getEntityStateByID(stateId: UUID): EntityState {
        return tryToExecute(
            { entityStateDataSource.getEntityStateById(stateId.toString()) },
            onSuccess = { it },
            onFailure = { throw NoEntityEntityStateFoundException() }
        )?.mapToStateEntity() ?: throw NoEntityEntityStateFoundException()
    }
}