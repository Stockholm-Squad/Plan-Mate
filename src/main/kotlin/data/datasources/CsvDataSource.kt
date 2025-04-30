package org.example.data.datasources

import org.jetbrains.kotlinx.dataframe.api.*
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
    override fun write(data: List<T>): Result<Boolean> {
        val file = resolveFile()

        if (data.isEmpty()) {
            file.writeText("")
            return Result.success(true)
        }

        // Convert to DataFrame using Kotlin reflection
        return data.first()::class.let { classType ->
            classType.memberProperties.map { it.name }.let { columnsNames ->
                columnsNames.map { colName ->
                    colName to data.map { item ->
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
}