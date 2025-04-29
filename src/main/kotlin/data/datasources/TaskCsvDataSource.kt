package org.example.data.datasources

import logic.model.entities.Task

class TaskCsvDataSource : PlanMateDataSource<Task> {
    override fun read(filePath: String): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun write(model: List<Task>): Result<Boolean> {
        TODO("Not yet implemented")
    }

}