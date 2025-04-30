package org.example.data.datasources

import logic.model.entities.Task
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV


class TaskCsvDataSource(private val filePath: String) : CsvDataSource<Task>(filePath) {
    override fun read(): Result<List<Task>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            DataFrame.readCSV(filePath).cast<Task>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }
}