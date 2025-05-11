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
            onSuccess = { success -> success },
            onFailure = { throw EntityStateNotAddedException() }
        )
    }

    override suspend fun editEntityState(entityState: EntityState): Boolean {
        return tryToExecute(
            { entityStateDataSource.editEntityState(entityState.mapToStateModel()) },
            onSuccess = { success -> success },
            onFailure = { throw EntityStateNotEditedException() }
        )
    }

    override suspend fun deleteEntityState(entityState: EntityState): Boolean {
        return tryToExecute(
            { entityStateDataSource.deleteEntityState(entityState.mapToStateModel()) },
            onSuccess = { success -> success },
            onFailure = { throw EntityStateNotDeletedException() }
        )

    }

    override suspend fun isEntityStateExist(stateName: String): Boolean {
        return tryToExecute(
            { entityStateDataSource.isEntityStateExist(stateName) },
            onSuccess = { success -> success },
            onFailure = { throw NoEntityStateFoundException() }
        )
    }

    override suspend fun getAllEntityStates(): List<EntityState> {
        return tryToExecute(
            { entityStateDataSource.getAllEntityStates() },
            onSuccess = { listOfEntityState -> listOfEntityState },
            onFailure = { throw NoEntityStatesFoundedException() }
        ).mapNotNull { it.mapToStateEntity() }
    }

    override suspend fun getEntityStateByName(stateName: String): EntityState {
        return tryToExecute(
            { entityStateDataSource.getEntityStateByName(stateName) },
            onSuccess = { listOfEntityState -> listOfEntityState },
            onFailure = { throw NoEntityStateFoundException() }
        )?.mapToStateEntity() ?: throw NoEntityStateFoundException()
    }

    override suspend fun getEntityStateByID(stateId: UUID): EntityState {
        return tryToExecute(
            { entityStateDataSource.getEntityStateById(stateId.toString()) },
            onSuccess = { entityState -> entityState },
            onFailure = { throw NoEntityStateFoundException() }
        )?.mapToStateEntity() ?: throw NoEntityStateFoundException()
    }
}