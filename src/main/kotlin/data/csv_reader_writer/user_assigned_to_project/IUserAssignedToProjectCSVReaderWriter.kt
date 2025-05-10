package org.example.data.csv_reader_writer.user_assigned_to_project

import data.dto.UserAssignedToProjectDto

interface IUserAssignedToProjectCSVReaderWriter {
    suspend fun read(): List<UserAssignedToProjectDto>
    suspend fun overWrite(users: List<UserAssignedToProjectDto>): Boolean
    suspend fun append(users: List<UserAssignedToProjectDto>): Boolean
}