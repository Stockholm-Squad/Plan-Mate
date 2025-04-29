package org.example.data.datasources


import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import kotlin.reflect.KClass

class CsvDataSource<T : Any>(
    private val filePath: String,
) : PlanMateDataSource<T> {

    override fun read(): Result<List<T>> {
        return try {

//            val df = DataFrame.readCSV(filePath)
//            val data: List<T> = createList(df)

            Result.success(emptyList())

        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun write(data: List<T>): Result<Boolean> {
        return try {
//            val dfRead = data.toDataFrame()
//            dfRead.writeCSV(filePath)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(PlanMateExceptions.DataException.WriteException())
        }
    }

}
