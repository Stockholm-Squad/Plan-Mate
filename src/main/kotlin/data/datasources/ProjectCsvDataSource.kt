package org.example.data.datasources

import logic.model.entities.Project
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.File

class ProjectCsvDataSource(private val filePath: String) : CsvDataSource<Project>(filePath) {
    override fun read(): Result<List<Project>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            if (File(filePath).readLines().size < 2)
                return Result.success(emptyList())

            DataFrame.readCSV(filePath).cast<Project>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }
}