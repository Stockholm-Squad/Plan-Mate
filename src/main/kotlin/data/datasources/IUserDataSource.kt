package org.example.data.datasources

import data.dto.UserModel

interface IUserDataSource {
    suspend fun read(): List<UserModel>
    suspend fun overWrite(users: List<UserModel>): Boolean
    suspend fun append(users: List<UserModel>): Boolean
}