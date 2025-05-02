package org.example.data.datasources.relations.user_assigned_to_project_data_source

import data.models.UserAssignedToProject

interface IUserAssignedToProjectDataSource {
    fun read(): Result<List<UserAssignedToProject>>
    fun overWrite(users: List<UserAssignedToProject>): Result<Boolean>
    fun append(users: List<UserAssignedToProject>): Result<Boolean>
}