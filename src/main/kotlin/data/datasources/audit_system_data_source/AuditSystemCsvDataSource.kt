package org.example.data.datasources.audit_system_data_source

import org.example.data.models.AuditSystemModel
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

class AuditSystemCsvDataSource(private val filePath: String) : IAuditSystemDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<AuditSystemModel>> {
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
                .cast<AuditSystemModel>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun overWrite(users: List<AuditSystemModel>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }

    override fun append(users: List<AuditSystemModel>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<AuditSystemModel>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
        }
    }
}
