package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: PlanMateDataSource<State>
) : StateRepository {

    companion object {
        var listOfStates = mutableListOf<State>()
    }

    override fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editState(state: State): Result<Boolean> {
        return getStates().fold(
            onSuccess = { data ->
                listOfStates.map {
                    if (it.id == state.id) state else it
                }.let {
                    stateDataSource.write(listOfStates)
                    Result.success(true)
                }
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun deleteState(id: String): Result<Boolean> {
        return stateDataSource.write(listOf()).fold(
            onSuccess = {
                listOfStates = mutableListOf()
                Result.success(true)
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }


    override fun getAllStates(): Result<List<State>> {
        TODO("Not yet implemented")
    }

    private fun getStates(): Result<List<State>> {
        return listOfStates.takeIf { it.isNotEmpty() }?.let {
            Result.success(listOfStates.toList())
        } ?: stateDataSource.read().fold(
            onSuccess = { data ->
                listOfStates = data.toMutableList()
                Result.success(listOfStates.toList())
            },
            onFailure = { exception -> this@StateRepositoryImp.handleReadException(exception) }
        )
    }

    private fun handleReadException(throwable: Throwable): Result<List<State>> {
        return throwable.takeIf { throwable is PlanMateExceptions.DataException }?.let { dataException ->
            dataException.takeIf { throwable is PlanMateExceptions.DataException.FileNotExistException }?.let {
                listOfStates = mutableListOf()
                Result.success(listOf())
            } ?: Result.failure(throwable)
        } ?: Result.failure(throwable)
    }
}