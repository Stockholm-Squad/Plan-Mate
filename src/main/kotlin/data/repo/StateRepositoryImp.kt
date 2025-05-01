package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: PlanMateDataSource<State>,
) : StateRepository {

    override fun addState(stateName: String): Result<Boolean> {
        return stateDataSource.append(listOf(State(name = stateName))).fold(
            onSuccess = { value ->
                Result.success(value)
            },
            onFailure = { exception ->  Result.failure(Throwable(exception)) }
        )
    }


    override fun editState(state: State): Result<Boolean> {
        return getAllStates().fold(
            onSuccess = { currentStates ->
                currentStates.map { item -> if (item.id == state.id) state else item }
                stateDataSource.overWrite(currentStates).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
                )
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun deleteState(state: State): Result<Boolean> {
        return getAllStates().fold(
            onSuccess = { currentStates ->
                currentStates.filterNot { it == state }
                stateDataSource.overWrite(currentStates).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
                )
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun getAllStates(): Result<List<State>> {
        return stateDataSource.read().fold(
            onSuccess = { allStates ->
                Result.success((allStates))
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

}