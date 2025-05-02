package org.example.data.datasources.user_data_source

import org.example.data.models.UserModel

interface IUserDataSource {
    fun read(): Result<List<UserModel>>
    fun overWrite(users: List<UserModel>): Result<Boolean>
    fun append(users: List<UserModel>): Result<Boolean>
}