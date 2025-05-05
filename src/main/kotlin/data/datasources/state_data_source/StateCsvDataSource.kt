package org.example.data.datasources.state_data_source

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

    override suspend fun read(): List<ProjectStateModel> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
        }

        if (File(filePath).readLines().size < 2)
            return emptyList()

        val users = DataFrame.readCSV(file)
            .cast<ProjectStateModel>()
            .toList()
        return users

    }

    override suspend fun overWrite(users: List<ProjectStateModel>): Boolean {
        users.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(users: List<ProjectStateModel>): Boolean {
        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<ProjectStateModel>().toDataFrame()

            val newData = users.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
            return true
        }
    }
}
