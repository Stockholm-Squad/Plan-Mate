package org.example

import org.example.data.datasources.user_data_source.UserCsvDataSource
import org.example.data.models.UserModel
import org.example.utils.hashToMd5


fun main() {
    val dataSource = UserCsvDataSource("users.csv")
    val adminUser = listOf(
        UserModel(id = "1", username = "rodina", hashedPassword = hashToMd5("admin123"), role = "ADMIN"),
    )
    val result = dataSource.overWrite(adminUser)
    result.onSuccess { println("Appended tasks successfully") }
        .onFailure { it.printStackTrace() }
    dataSource.read().onSuccess { println("Current tasks:\n$it") }
        .onFailure { it.printStackTrace() }
}