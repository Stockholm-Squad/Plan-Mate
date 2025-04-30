package org.example.data.datasources

import org.example.data.entities.MateTaskAssignment
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV


class MateTaskAssignmentCsvDataSource(private val filePath: String) : CsvDataSource<MateTaskAssignment>(filePath) {
    override fun read(): Result<List<MateTaskAssignment>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            DataFrame.readCSV(filePath).cast<MateTaskAssignment>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }
}