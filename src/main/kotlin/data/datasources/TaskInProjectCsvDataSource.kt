package org.example.data.datasources

import org.example.data.entities.TaskInProject

class TaskInProjectCsvDataSource : PlanMateDataSource<TaskInProject> {
    override fun read(): Result<List<TaskInProject>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

    override fun append(model: List<TaskInProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<TaskInProject>): Result<Boolean> {
        TODO("Not yet implemented")
    }
        return try {
            DataFrame.readCSV(filePath).cast<TaskInProject>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
}