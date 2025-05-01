package org.example.data.datasources

import logic.model.entities.User
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV

class UserCsvDataSource(private val filePath: String) : CsvDataSource<User>(filePath) {
    override fun read(): Result<List<User>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }
        return try {
            DataFrame.readCSV(filePath).cast<User>().toList().let {
                Result.success(it)
            }
        } catch (t: Exception) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }
}