package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProject
import org.example.logic.model.exceptions.FileNotExistException
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.WriteDataException

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

    override fun read(): Result<List<UserAssignedToProject>> {
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
                .cast<UserAssignedToProject>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(ReadDataException())
        }
    }

    override fun overWrite(users: List<UserAssignedToProject>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }

    override fun append(users: List<UserAssignedToProject>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<UserAssignedToProject>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }
}
