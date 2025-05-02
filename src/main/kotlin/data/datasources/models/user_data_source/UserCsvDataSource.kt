package org.example.data.datasources.models.user_data_source

import logic.model.entities.User
import org.example.data.models.UserModel
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList

class UserCsvDataSource(private val filePath: String) : IUserDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<UserModel>> {
        val file = resolveFile()
        if (!file.exists()) {
            return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            val users = DataFrame.readCSV(file)
                .cast<UserModel>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun overWrite(users: List<UserModel>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }

    override fun append(users: List<UserModel>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                }
                else emptyList<UserModel>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }
}
