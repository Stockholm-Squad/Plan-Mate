package org.example.data.datasources

import logic.model.entities.AuditSystem
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV

class AuditSystemCsvDataSource(private val filePath: String) : CsvDataSource<AuditSystem>(filePath) {
    override fun read(): Result<List<AuditSystem>> {
        super.resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            DataFrame.readCSV(filePath).cast<AuditSystem>().toList().let {
                Result.success(it)
            }
        } catch (t: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun append(model: List<AuditSystem>): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override fun overWrite(model: List<AuditSystem>): Result<Boolean> {
        TODO("Not yet implemented")

    }

}