package org.example.data.datasources

import data.models.UserAssignedToProjectModel

interface IUserAssignedToProjectDataSource {
    suspend fun read(): List<UserAssignedToProjectModel>
    suspend fun overWrite(users: List<UserAssignedToProjectModel>): Boolean
    suspend fun append(users: List<UserAssignedToProjectModel>): Boolean
}