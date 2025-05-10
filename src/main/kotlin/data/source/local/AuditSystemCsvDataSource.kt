package org.example.data.source.local

import org.example.data.datasources.IAuditSystemDataSource
import org.example.data.models.AuditSystemModel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class AuditSystemCsvDataSource(private val filePath: String) : IAuditSystemDataSource {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<AuditSystemModel> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<AuditSystemModel>()
            .toList()
    }

    override suspend fun overWrite(audits: List<AuditSystemModel>): Boolean {
        audits.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(audits: List<AuditSystemModel>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<AuditSystemModel>().toDataFrame()
        }

        val newData = audits.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
