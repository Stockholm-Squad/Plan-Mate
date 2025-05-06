package org.example.data.datasources.user_data_source

import org.example.data.models.UserModel

interface IUserDataSource {
    suspend fun read(): List<UserModel>
    suspend fun overWrite(users: List<UserModel>): Boolean
    suspend fun append(users: List<UserModel>): Boolean
}