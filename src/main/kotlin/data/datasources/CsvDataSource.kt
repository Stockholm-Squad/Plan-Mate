package org.example.data.datasources

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

abstract class CsvDataSource<T : Any>(
    private val filePath: String,
) : PlanMateDataSource<T> {

    protected fun resolveFile(): File = File(filePath)

    abstract override fun read(): Result<List<T>>

    @Suppress("UNCHECKED_CAST")
    override fun write(model: List<T>): Result<Boolean> {
        val file = resolveFile()

        if (model.isEmpty()) {
            file.writeText("")
            return Result.success(true)
        }

        // Convert to DataFrame using Kotlin reflection
        return model.first()::class.let { classType ->
            classType.memberProperties.map { it.name }.let { columnsNames ->
                columnsNames.map { colName ->
                    colName to model.map { item ->
                        (classType.memberProperties.first { it.name == colName } as KProperty1<T, *>)
                            .apply { isAccessible = true }
                            .get(item)
                    }
                }.let { columns ->
                    dataFrameOf(*columns.toTypedArray()).also {
                        it.writeCSV(file)
                    }.let {
                        Result.success(true)
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun append(model: List<T>): Result<Boolean> {
        val file = resolveFile()

        if (model.isEmpty()) {
            return Result.success(true)
        }

        return runCatching {
            val classType = model.first()::class
            val columnsNames = classType.memberProperties.map { it.name }
            val columns = columnsNames.map { colName ->
                colName to model.map { item ->
                    (classType.memberProperties.first { it.name == colName } as KProperty1<T, *>)
                        .apply { isAccessible = true }
                        .get(item)
                }
            }

            val newDf = dataFrameOf(*columns.toTypedArray())
            val finalDf = if (file.exists() && file.length() > 0) {
                val existingDf = DataFrame.readCSV(file)
                existingDf.concat(newDf)
            } else {
                newDf
            }

            finalDf.writeCSV(file)
            true
        }
    }
}


//fun main() {
//    val dataSource = ProjectCsvDataSource("projects.csv")
//
//    val tasks = listOf(
//        Project("3", "Call mom"," false"),
//        Project("4", "Clean desk"," true")
//    )
//
//    val result = dataSource.append(tasks)
//    result.onSuccess { println("Appended tasks successfully") }
//        .onFailure { it.printStackTrace() }
//
//    dataSource.read().onSuccess { println("Current tasks:\n$it") }
//        .onFailure { it.printStackTrace() }
//}
