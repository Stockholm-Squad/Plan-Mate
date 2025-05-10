package org.example.data.utils.csv_reader_writer.mate_task_assignment

import data.dto.MateTaskAssignmentDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class MateTaskAssignmentCSVReaderWriter(private val filePath: String) : IMateTaskAssignmentCSVReaderWriter {

    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<MateTaskAssignmentDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<MateTaskAssignmentDto>()
            .toList()
    }

    override suspend fun overWrite(mateTasks: List<MateTaskAssignmentDto>): Boolean {
        mateTasks.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(mateTasks: List<MateTaskAssignmentDto>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<MateTaskAssignmentDto>().toDataFrame()
        }

        val newData = mateTasks.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
