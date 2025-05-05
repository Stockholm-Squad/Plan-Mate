package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProjectModel
import logic.models.exceptions.FileNotExistException
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class UserAssignedToProjectCsvDataSource(private val filePath: String) : IUserAssignedToProjectDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<UserAssignedToProjectModel>> {
        val file = resolveFile()
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (throwable: Throwable) {
                return Result.failure(FileNotExistException())
            }
        }

        return try {
            if (File(filePath).readLines().size < 2)
                return Result.success(emptyList())

            val users = DataFrame.readCSV(file)
                .cast<UserAssignedToProjectModel>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(ReadDataException())
        }
    }

    override fun overWrite(users: List<UserAssignedToProjectModel>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }

    override fun append(users: List<UserAssignedToProjectModel>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<UserAssignedToProjectModel>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }
}
