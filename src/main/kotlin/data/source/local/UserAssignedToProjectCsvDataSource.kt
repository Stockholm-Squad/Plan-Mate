package org.example.data.source.local

import data.dto.UserAssignedToProjectModel
import org.example.data.datasources.IUserAssignedToProjectDataSource
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

    override suspend fun read(): List<UserAssignedToProjectModel> {
        val file = resolveFile()

        if (!file.exists()) {
            file.createNewFile()
        }

        if (File(filePath).readLines().size < 2)
            return emptyList()

        val users = DataFrame.readCSV(file)
            .cast<UserAssignedToProjectModel>()
            .toList()
        return users
    }

    override suspend fun overWrite(users: List<UserAssignedToProjectModel>): Boolean {
        users.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(users: List<UserAssignedToProjectModel>): Boolean {
        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<UserAssignedToProjectModel>().toDataFrame()

            val newData = users.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true
    }
}
