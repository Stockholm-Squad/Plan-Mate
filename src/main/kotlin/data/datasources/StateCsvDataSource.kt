package org.example.data.datasources

import logic.model.entities.State

class StateCsvDataSource : PlanMateDataSource<State> {
    override fun read(): Result<List<State>> {
        return Result.success(listOf())
    }

    override fun write(model: List<State>): Result<Boolean> {
        return Result.success(false)
    }

}