package data.datasources.models.project_data_source

import org.example.data.datasources.models.project_data_source.IProjectDataSource
import org.example.data.models.Project
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File

class ProjectCsvDataSource(private val filePath: String) : IProjectDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<Project>> {
        val file = resolveFile()
        if (!file.exists()) {
            return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            val users = DataFrame.readCSV(file)
                .cast<Project>()
                .toList()
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun overWrite(users: List<Project>): Result<Boolean> {
        return try {
            users.toDataFrame().writeCSV(resolveFile())
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }

    override fun append(users: List<Project>): Result<Boolean> {
        return try {
            resolveFile().also { file ->
                val existing = if (file.exists() && file.length() > 0) {
                    DataFrame.readCSV(file).cast()
                } else emptyList<Project>().toDataFrame()

                val newData = users.toDataFrame()
                (existing.concat(newData)).writeCSV(file)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }
}
