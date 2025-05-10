package org.example.data.datasources

import data.dto.UserAssignedToProjectDto

interface IUserAssignedToProjectDataSource {
    suspend fun read(): List<UserAssignedToProjectDto>
    suspend fun overWrite(users: List<UserAssignedToProjectDto>): Boolean
    suspend fun append(users: List<UserAssignedToProjectDto>): Boolean
}