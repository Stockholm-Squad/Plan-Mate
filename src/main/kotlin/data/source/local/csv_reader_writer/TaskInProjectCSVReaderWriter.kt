package org.example.data.source.local.csv_reader_writer

import data.dto.TaskInProjectDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class TaskInProjectCSVReaderWriter(private val filePath: String) : IReaderWriter<TaskInProjectDto> {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<TaskInProjectDto> {
        val file = resolveFile()
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
        }


        if (File(filePath).readLines().size < 2)
            return emptyList()

        val tasks = DataFrame.readCSV(file)
            .cast<TaskInProjectDto>()
            .toList()
        return tasks
    }

    override suspend fun overWrite(data: List<TaskInProjectDto>): Boolean {
        data.toDataFrame().writeCSV(resolveFile())
        return true

    }

    override suspend fun append(data: List<TaskInProjectDto>): Boolean {

        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<TaskInProjectDto>().toDataFrame()

            val newData = data.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true
    }
}
