package org.example.data.datasources

import data.dto.UserDto

interface IUserCSVReaderWriter {
    suspend fun read(): List<UserDto>
    suspend fun overWrite(users: List<UserDto>): Boolean
    suspend fun append(users: List<UserDto>): Boolean
}