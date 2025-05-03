package org.example.data.datasources.user_data_source

import org.example.data.models.UserModel
import org.example.logic.model.exceptions.FileNotExistException
import org.example.logic.model.exceptions.ReadDataException
import org.example.logic.model.exceptions.WriteDataException
import org.example.logic.utils.hashToMd5
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import java.util.UUID

class UserCsvDataSource(private val filePath: String) : IUserDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<UserModel>> {
        val file = resolveFile()
        if (!file.exists()) {
            return Result.failure(FileNotExistException())
        }

        return try {
            if (File(filePath).readLines().size < 2) {
                val adminUser = listOf(
                    UserModel(id = UUID.randomUUID().toString(), username = "rodina", hashedPassword = hashToMd5("admin123"), role = "ADMIN"),
                )
                overWrite(adminUser).onFailure { return Result.failure(it) }
            }

            val users = DataFrame.readCSV(file)
                .cast<UserModel>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(ReadDataException())
        }
    }

    override fun overWrite(users: List<UserModel>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(WriteDataException())
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
            Result.failure(WriteDataException())
        }
    }
}
