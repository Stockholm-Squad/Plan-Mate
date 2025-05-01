package org.example.data.datasources

import org.example.data.entities.TaskInProject
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.File


class TaskInProjectCsvDataSource(private val filePath: String) : CsvDataSource<TaskInProject>(filePath) {
    override fun read(): Result<List<TaskInProject>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            if (File(filePath).readLines().size < 2)
                return Result.success(emptyList())

            DataFrame.readCSV(filePath).cast<TaskInProject>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }
        return try {
            DataFrame.readCSV(filePath).cast<TaskInProject>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
}