package org.example.data.datasources.models.user_data_source

import org.example.data.models.User


interface IUserDataSource {
    fun read(): Result<List<User>>
    fun overWrite(users: List<User>): Result<Boolean>
    fun append(users: List<User>): Result<Boolean>
}