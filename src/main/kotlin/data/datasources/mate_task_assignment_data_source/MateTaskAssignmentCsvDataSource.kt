package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File


class MateTaskAssignmentCsvDataSource(private val filePath: String) : IMateTaskAssignmentDataSource {

    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<MateTaskAssignmentModel> {
        val file = resolveFile()
        if (!file.exists()) {
            file.createNewFile()
            return emptyList()
        }

        if (file.readLines().size < 2) {
            return emptyList()
        }

        return DataFrame.readCSV(file)
            .cast<MateTaskAssignmentModel>()
            .toList()
    }

    override suspend fun overWrite(mateTasks: List<MateTaskAssignmentModel>): Boolean {
        mateTasks.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(mateTasks: List<MateTaskAssignmentModel>): Boolean {
        val file = resolveFile()
        val existing = if (file.exists() && file.length() > 0) {
            DataFrame.readCSV(file).cast()
        } else {
            emptyList<MateTaskAssignmentModel>().toDataFrame()
        }

        val newData = mateTasks.toDataFrame()
        (existing.concat(newData)).writeCSV(file)
        return true
    }

}
