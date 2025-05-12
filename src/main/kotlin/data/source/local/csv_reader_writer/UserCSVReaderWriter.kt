package org.example.data.source.local.csv_reader_writer

import data.dto.UserDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.logic.utils.HashingService
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.writeCSV
import java.io.File
import java.util.*

class UserCSVReaderWriter(
    private val filePath: String,
    private val hashingService: HashingService,
) : IReaderWriter<UserDto> {
    private fun resolveFile(): File = File(filePath)

    override suspend fun read(): List<UserDto> {
        val file = resolveFile()
        if (!file.exists()) {
            withContext(Dispatchers.IO) {
                file.createNewFile()
            }
            createAdminIfNotExist()
        }

        createAdminIfNotExist()
        return DataFrame.readCSV(file)
            .cast<UserDto>()
            .toList()
    }


    private suspend fun createAdminIfNotExist(): Boolean {
        if (File(filePath).readLines().size < 2) {
            val adminUser = listOf(
                UserDto(
                    id = UUID.randomUUID().toString(),
                    username = "rodina",
                    hashedPassword = hashingService.hash("admin123"),
                    role = "ADMIN"
                ),
            )
            overWrite(adminUser)
        }
        return true
    }

    override suspend fun overWrite(data: List<UserDto>): Boolean {
        data.toDataFrame().writeCSV(resolveFile())
        return true
    }

    override suspend fun append(data: List<UserDto>): Boolean {
        resolveFile().also { file ->
            val existing = if (file.exists() && file.length() > 0) {
                DataFrame.readCSV(file).cast()
            } else emptyList<UserDto>().toDataFrame()

            val newData = data.toDataFrame()
            (existing.concat(newData)).writeCSV(file)
        }
        return true

    }
}
