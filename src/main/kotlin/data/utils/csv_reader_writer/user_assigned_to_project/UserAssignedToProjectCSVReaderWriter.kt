package org.example.data.utils.csv_reader_writer.user_assigned_to_project

import data.dto.UserAssignedToProjectDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class UserAssignedToProjectCSVReaderWriter(private val filePath: String) : IUserAssignedToProjectCSVReaderWriter {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<UserAssignedToProjectDto> {
        val file = resolveFile()

        if (!file.exists()) {
            file.createNewFile()
        }

        if (File(filePath).readLines().size < 2)
            return emptyList()

        val users = DataFrame.readCSV(file)
            .cast<UserAssignedToProjectDto>()
            .toList()
        return users
    }

    override suspend fun overWrite(users: List<UserAssignedToProjectDto>): Boolean {
        users.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(users: List<UserAssignedToProjectDto>): Boolean {
        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<UserAssignedToProjectDto>().toDataFrame()

            val newData = users.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true
    }
}
