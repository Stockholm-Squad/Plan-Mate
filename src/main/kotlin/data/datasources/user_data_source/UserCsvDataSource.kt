package org.example.data.datasources.user_data_source

import logic.model.entities.Role
import logic.model.entities.User
import org.example.data.datasources.CsvDataSource
import org.example.logic.model.exceptions.PlanMateExceptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class UserCsvDataSource(private val filePath: String) : UserDataSource {
    private fun resolveFile(): File = File(filePath)

    override fun read(): Result<List<User>> {
        val file = resolveFile().also {
            if (!it.exists()) return Result.failure(PlanMateExceptions.DataException.FileNotExistException())
        }

        return try {
            val lines = file.readLines()
            if (lines.isEmpty()) return Result.success(emptyList())

            val header = lines.first().split(",")
            val expectedHeader = listOf("id", "name", "email")
            if (header != expectedHeader) {
                throw PlanMateExceptions.DataException.ReadException()
            }

            lines.drop(1)
                .map { line ->
                    val values = line.split(",")
                    User(
                        username = values[0].trim(),
                        hashedPassword = values[1].trim(),
                        role = if (values[2].trim() == "ADMIN") Role.ADMIN else Role.MATE
                    )
                }
                .let { Result.success(it) }
        }catch (throwable: Throwable) {
            Result.failure(PlanMateExceptions.DataException.ReadException())
        }
    }

    override fun overWrite(users: List<User>): Result<Boolean> {
        val file = resolveFile()

        return runCatching {
            val rows = users.map { user ->
                listOf(user.username, user.hashedPassword, user.role)
            }

            val header = listOf("id", "name", "email")
            val csvContent = (listOf(header) + rows)
                .joinToString("\n") { row -> row.joinToString(",") }

            file.writeText(csvContent)
            true
        }
    }

    override fun append(users: List<User>): Result<Boolean> {
        val file = resolveFile()

        return runCatching {
            if (users.isEmpty()) return@runCatching true

            val rows = users.map { user ->
                listOf(user.username, user.hashedPassword, user.role)
            }

            val csvContent = rows.joinToString("\n") { row -> row.joinToString(",") }

            if (file.exists() && file.length() > 0) {
                file.appendText("\n$csvContent")
            } else {
                val header = listOf("id", "name", "email")
                file.writeText(header.joinToString(",") + "\n" + csvContent)
            }

            true
        }
    }
}