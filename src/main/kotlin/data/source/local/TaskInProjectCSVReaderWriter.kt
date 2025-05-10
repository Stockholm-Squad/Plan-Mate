package org.example.data.source.local

import data.dto.TaskInProjectDto
import org.example.data.datasources.ITaskInProjectCSVReaderWriter
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class TaskInProjectCSVReaderWriter(private val filePath: String) : ITaskInProjectCSVReaderWriter {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<TaskInProjectDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
        }


        if (File(filePath).readLines().size < 2)
            return emptyList()

        val tasks = DataFrame.readCSV(file)
            .cast<TaskInProjectDto>()
            .toList()
        return tasks
    }

    override suspend fun overWrite(tasks: List<TaskInProjectDto>): Boolean {
        tasks.toDataFrame().writeCSV(resolveFile())
        return true

    }

    override suspend fun append(tasks: List<TaskInProjectDto>): Boolean {

        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<TaskInProjectDto>().toDataFrame()

            val newData = tasks.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true
    }
}
