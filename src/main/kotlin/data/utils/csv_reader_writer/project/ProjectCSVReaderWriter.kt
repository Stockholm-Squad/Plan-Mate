package org.example.data.utils.csv_reader_writer.project


import data.dto.ProjectDto
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class ProjectCSVReaderWriter(private val filePath: String) : IProjectCSVReaderWriter {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<ProjectDto> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<ProjectDto>()
            .toList()
    }

    override suspend fun overWrite(projects: List<ProjectDto>): Boolean {
        projects.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(projects: List<ProjectDto>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<ProjectDto>().toDataFrame()
        }

        val newData = projects.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
