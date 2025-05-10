package org.example.data.utils.csv_reader_writer.user

import data.dto.UserDto

interface IUserCSVReaderWriter {
    suspend fun read(): List<UserDto>
    suspend fun overWrite(users: List<UserDto>): Boolean
    suspend fun append(users: List<UserDto>): Boolean
}