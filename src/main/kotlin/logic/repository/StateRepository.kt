package org.example.logic.repository

import logic.model.entities.State

interface StateRepository {
    fun addState(state: State): Result<Boolean>
    fun editState(state: State): Result<Boolean>
    fun deleteState(id: String): Result<Boolean>
    fun getAllStates(): Result<List<State>>
}