package org.example.data.source.local.csv_reader_writer

import data.dto.MateTaskAssignmentDto
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


class MateTaskAssignmentCSVReaderWriter(private val filePath: String) : IReaderWriter<MateTaskAssignmentDto> {

    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<MateTaskAssignmentDto> {
        val file = resolveFile()
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<MateTaskAssignmentDto>()
            .toList()
    }

    override suspend fun overWrite(data: List<MateTaskAssignmentDto>): Boolean {
        data.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(data: List<MateTaskAssignmentDto>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<MateTaskAssignmentDto>().toDataFrame()
        }

        val newData = data.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
