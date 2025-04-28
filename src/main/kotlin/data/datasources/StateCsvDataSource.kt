package org.example.data.datasources

import logic.model.entities.State

class StateCsvDataSource : PlanMateDataSource<State> {
    override fun read(filePath: String): Result<List<State>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<State>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}