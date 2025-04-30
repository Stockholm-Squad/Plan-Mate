package org.example.data.datasources

import logic.model.entities.Task

class TaskCsvDataSource : PlanMateDataSource<Task> {
    override fun read(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun append(model: List<Task>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<Task>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}