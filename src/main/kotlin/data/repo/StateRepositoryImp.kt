package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.state_data_source.IStateDataSource
import org.example.data.mapper.StateMapper
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: IStateDataSource,
    private val stateMapper: StateMapper,
) : StateRepository {

    override fun addState(stateName: String): Result<Boolean> {
        return stateDataSource.append(listOf(stateMapper.mapToStateModel(State(name = stateName))))
    }

    override fun editState(state: State): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                currentStates.map { item -> if (item.id == state.id.toString()) state else item }
                stateDataSource.overWrite(currentStates)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun deleteState(state: State): Result<Boolean> {
        return stateDataSource.read().fold(
            onSuccess = { currentStates ->
                currentStates.filterNot { it == stateMapper.mapToStateModel(state) }
                stateDataSource.overWrite(currentStates)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun getAllStates(): Result<List<State>> {
        return stateDataSource.read().fold(
            onSuccess = { allStates ->
                Result.success(allStates.map { it1 -> stateMapper.mapToStateEntity(it1) })
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

}