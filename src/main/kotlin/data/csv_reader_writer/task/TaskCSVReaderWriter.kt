package org.example.data.csv_reader_writer.task

import data.dto.TaskDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class TaskCSVReaderWriter(private val filePath: String) : ITaskCSVReaderWriter {

    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<TaskDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<TaskDto>()
            .toList()
    }

    override suspend fun overWrite(tasks: List<TaskDto>): Boolean {
        tasks.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(tasks: List<TaskDto>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<TaskDto>().toDataFrame()
        }

        val newData = tasks.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
