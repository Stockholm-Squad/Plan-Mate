package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignment
import org.example.logic.model.exceptions.FileNotExistException
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.WriteDataException
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList


class MateTaskAssignmentCsvDataSource(private val filePath: String) : IMateTaskAssignmentDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<MateTaskAssignment>> {
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
                .cast<MateTaskAssignment>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(ReadDataException())
        }
    }

    override fun overWrite(users: List<MateTaskAssignment>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }

    override fun append(users: List<MateTaskAssignment>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<MateTaskAssignment>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }
}
