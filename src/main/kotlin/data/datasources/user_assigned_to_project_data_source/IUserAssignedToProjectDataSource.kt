package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProjectModel

interface IUserAssignedToProjectDataSource {
    suspend fun read(): List<UserAssignedToProjectModel>
    suspend fun overWrite(users: List<UserAssignedToProjectModel>): Boolean
    suspend fun append(users: List<UserAssignedToProjectModel>): Boolean
}