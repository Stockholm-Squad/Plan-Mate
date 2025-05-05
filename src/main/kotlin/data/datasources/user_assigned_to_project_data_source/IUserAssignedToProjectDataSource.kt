package org.example.data.datasources.user_assigned_to_project_data_source

import data.models.UserAssignedToProjectModel

interface IUserAssignedToProjectDataSource {
    fun read(): List<UserAssignedToProjectModel>
    fun overWrite(users: List<UserAssignedToProjectModel>): Boolean
    fun append(users: List<UserAssignedToProjectModel>): Boolean
}