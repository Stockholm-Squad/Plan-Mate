package org.example.data.datasources.models.user_data_source

import logic.model.entities.User

interface IUserDataSource {
    fun read(): Result<List<User>>
    fun overWrite(users: List<User>): Result<Boolean>
    fun append(users: List<User>): Result<Boolean>
}