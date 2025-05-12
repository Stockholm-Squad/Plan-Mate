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

    override suspend fun addEntityState(entityState: EntityState): Boolean = tryToExecute(
        { entityStateDataSource.addEntityState(entityState.mapToStateModel()) },
        onSuccess = { isAdded -> isAdded },
        onFailure = { throw EntityStateNotAddedException() }
    )


    override suspend fun updateEntityState(entityState: EntityState): Boolean = tryToExecute(
        { entityStateDataSource.updateEntityState(entityState.mapToStateModel()) },
        onSuccess = { isUpdated -> isUpdated },
        onFailure = { throw EntityStateNotUpdatedException() }
    )


    override suspend fun deleteEntityState(entityState: EntityState): Boolean = tryToExecute(
        { entityStateDataSource.deleteEntityState(entityState.mapToStateModel()) },
        onSuccess = { isDeleted -> isDeleted },
        onFailure = { throw EntityStateNotDeletedException() }
    )


    override suspend fun isEntityStateExist(stateName: String): Boolean = tryToExecute(
        { entityStateDataSource.isEntityStateExist(stateName) },
        onSuccess = { isExist -> isExist },
        onFailure = { throw NoEntityStateFoundException() }
    )


    override suspend fun getAllEntityStates(): List<EntityState> = tryToExecute(
        { entityStateDataSource.getAllEntityStates() },
        onSuccess = { listOfEntityState -> listOfEntityState },
        onFailure = { throw NoEntityStatesFoundedException() }
    ).mapNotNull { it.mapToStateEntity() }


    override suspend fun getEntityStateByName(stateName: String): EntityState = tryToExecute(
        { entityStateDataSource.getEntityStateByName(stateName) },
        onSuccess = { listOfEntityState -> listOfEntityState },
        onFailure = { throw NoEntityStateFoundException() }
    )?.mapToStateEntity() ?: throw NoEntityStateFoundException()


    override suspend fun getEntityStateByID(stateId: UUID): EntityState = tryToExecute(
        { entityStateDataSource.getEntityStateById(stateId.toString()) },
        onSuccess = { entityState -> entityState },
        onFailure = { throw NoEntityStateFoundException() }
    )?.mapToStateEntity() ?: throw NoEntityStateFoundException()
}
