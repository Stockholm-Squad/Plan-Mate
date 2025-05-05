package org.example.data.datasources.state_data_source

import logic.models.exceptions.FileNotExistException
import logic.models.exceptions.ReadDataException
import logic.models.exceptions.WriteDataException
import org.example.data.models.ProjectStateModel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class StateCsvDataSource(private val filePath: String) : IStateDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<ProjectStateModel>> {
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
                .cast<ProjectStateModel>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(ReadDataException())
        }
    }

    override fun overWrite(users: List<ProjectStateModel>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }

    override fun append(users: List<ProjectStateModel>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<ProjectStateModel>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }
}
