package org.example.data.datasources

import logic.model.entities.Task

class TaskCsvDataSource : PlanMateDataSource<Task> {
    override fun read(): Result<List<Task>> {
        super.resolveFile().also {
            if (!it.exists()){ return Result.failure(PlanMateExceptions.DataException.FileNotExistException()) }
        }

    override fun append(model: List<Task>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<Task>): Result<Boolean> {
        TODO("Not yet implemented")
    }

        return try {
            DataFrame.readCSV(filePath).cast<Task>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }

}