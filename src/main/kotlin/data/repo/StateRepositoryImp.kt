package org.example.data.repo

import logic.model.entities.State
import org.example.data.datasources.PlanMateDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.example.logic.repository.StateRepository

class StateRepositoryImp(
    private val stateDataSource: PlanMateDataSource<State>
) : StateRepository {

    private var listOfStates = mutableListOf<State>()

    init {
        getAllStates()
    }

    override fun addState(state: State): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun editState(state: State): Result<Boolean> {
        return getAllStates().fold(
            onSuccess = { allStates ->
                allStates.map { item -> if (item.id == state.id) state else item }
                    .takeIf { it.isNotEmpty() && it.contains(state) }?.let {
                        stateDataSource.write(allStates).fold(
                            onSuccess = { stateEdited -> Result.success(stateEdited) },
                            onFailure = { exception ->
                                listOfStates.add(state)
                                Result.failure(exception)
                            }
                        )
                    } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
            },
            onFailure = { Result.failure(exception = it) }
        )

    }

    override fun deleteState(id: String): Result<Boolean> {
        return getAllStates().fold(
            onSuccess = {
                it.takeIf { it.isNotEmpty() }?.let {
                    stateDataSource.write(listOf()).fold(
                        onSuccess = {
                            listOfStates = mutableListOf()
                            Result.success(true)
                        },
                        onFailure = { exception -> Result.failure(exception) }
                    )
                } ?: Result.failure(PlanMateExceptions.LogicException.StateNotExistException())
            },
            onFailure = { exception -> Result.failure(exception) }
        )
    }

    override fun getAllStates(): Result<List<State>> {
        return listOfStates.takeIf { it.isNotEmpty() }
            ?.let {
                Result.success(it)
            } ?: stateDataSource.read().fold(
            onSuccess = {
                listOfStates = it.toMutableList()
                Result.success(listOfStates)
            },
            onFailure = { exception -> this@StateRepositoryImp.handleReadException(exception) }
        )
    }

    private fun handleReadException(throwable: Throwable): Result<List<State>> {
        return throwable.takeIf { throwable is PlanMateExceptions.DataException }?.let { dataException ->
            dataException.takeIf { throwable is PlanMateExceptions.DataException.FileNotExistException }?.let {
                listOfStates = mutableListOf()
                Result.failure(PlanMateExceptions.DataException.EmptyDataException())
            } ?: Result.failure(throwable)
        } ?: Result.failure(throwable)
    }
}