package org.example.data.datasources.user_data_source

import logic.model.entities.User

interface UserDataSource {
    fun read(): Result<List<User>>
    fun overWrite(users: List<User>): Result<Boolean>
    fun append(users: List<User>): Result<Boolean>
}