package org.example.data.csv_reader_writer.audits

import data.dto.AuditDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class AuditCSVReaderWriter(private val filePath: String) : IAuditCSVReaderWriter {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<AuditDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<AuditDto>()
            .toList()
    }

    override suspend fun overWrite(audits: List<AuditDto>): Boolean {
        audits.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(audits: List<AuditDto>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<AuditDto>().toDataFrame()
        }

        val newData = audits.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
