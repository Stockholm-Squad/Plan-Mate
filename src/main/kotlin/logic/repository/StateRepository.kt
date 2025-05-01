package org.example.logic.repository

import logic.model.entities.State

interface StateRepository {
    fun addState(stateName: String): Result<Boolean>
    fun editState(state: State): Result<Boolean>
    fun deleteState(state: State): Result<Boolean>
    fun getAllStates(): Result<List<State>>
}