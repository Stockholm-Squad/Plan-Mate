package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProject
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class TaskInProjectCsvDataSource(private val filePath: String) : ITaskInProjectDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<TaskInProject>> {
        val file = resolveFile()
        if (!file.exists()) {
            return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            if (File(filePath).readLines().size < 2)
                return Result.success(emptyList())

            val users = DataFrame.readCSV(file)
                .cast<TaskInProject>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun overWrite(users: List<TaskInProject>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }

    override fun append(users: List<TaskInProject>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<TaskInProject>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }
}
