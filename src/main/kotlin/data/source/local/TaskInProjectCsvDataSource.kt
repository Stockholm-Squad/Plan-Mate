package org.example.data.source.local

import data.dto.TaskInProjectModel
import org.example.data.datasources.ITaskInProjectDataSource
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class TaskInProjectCsvDataSource(private val filePath: String) : ITaskInProjectDataSource {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<TaskInProjectModel> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
        }


        if (File(filePath).readLines().size < 2)
            return emptyList()

        val tasks = DataFrame.readCSV(file)
            .cast<TaskInProjectModel>()
            .toList()
        return tasks
    }

    override suspend fun overWrite(tasks: List<TaskInProjectModel>): Boolean {
        tasks.toDataFrame().writeCSV(resolveFile())
        return true

    }

    override suspend fun append(tasks: List<TaskInProjectModel>): Boolean {

        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<TaskInProjectModel>().toDataFrame()

            val newData = tasks.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true
    }
}
