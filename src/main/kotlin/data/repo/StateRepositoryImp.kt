package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: PlanMateDataSource<State>,
) : StateRepository {

    init {
        getAllStates()
    }

    override fun addState(stateName: String): Result<Boolean> {
        return stateDataSource.write(listOf(State(name = stateName))).fold(
            onSuccess = {value ->
                Result.success(value)
            },
            onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
        )
    }


    override fun editState(state: State): Result<Boolean> {
        return getAllStates().fold(
            onSuccess = { currentStates ->
                val updatedStates = currentStates.map {
                    if (it.id == state.id) state else it
                }

                stateDataSource.write(updatedStates).fold(
                    onSuccess = { Result.success(true) },
                    onFailure = { Result.failure(PlanMateExceptions.DataException.WriteException()) }
                )
            },
            onFailure = {exception ->  Result.failure(exception) }
        )
    }

    override fun deleteState(id: String): Result<Boolean> {
        TODO()
//        return listOfStates.takeIf { it.isNotEmpty() }?.let {
//            stateDataSource.write(listOf()).fold(
//                onSuccess = {
//                    listOfStates = mutableListOf()
//                    Result.success(true)
//                },
//                onFailure = { exception -> Result.failure(exception) }
//            )
//        } ?: Result.failure(PlanMateExceptions.DataException.EmptyDataException())
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