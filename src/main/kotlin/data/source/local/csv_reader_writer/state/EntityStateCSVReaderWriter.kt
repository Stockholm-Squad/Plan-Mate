package org.example.data.source.local.csv_reader_writer.state

import data.dto.EntityStateDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class EntityStateCSVReaderWriter(private val filePath: String) :
    IEntityStateCSVReaderWriter {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<EntityStateDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
        }

        if (File(filePath).readLines().size < 2)
            return emptyList()

        val users = DataFrame.readCSV(file)
            .cast<EntityStateDto>()
            .toList()
        return users

    }

    override suspend fun overWrite(state: List<EntityStateDto>): Boolean {
        state.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(state: List<EntityStateDto>): Boolean {
        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<EntityStateDto>().toDataFrame()

            val newData = state.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
            return true
        }
    }
}
