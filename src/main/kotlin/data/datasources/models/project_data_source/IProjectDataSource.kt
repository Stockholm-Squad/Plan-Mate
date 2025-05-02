package org.example.data.datasources.models.project_data_source

import org.example.data.models.Project

interface IProjectDataSource {
    fun read(): Result<List<Project>>
    fun overWrite(users: List<Project>): Result<Boolean>
    fun append(users: List<Project>): Result<Boolean>
}