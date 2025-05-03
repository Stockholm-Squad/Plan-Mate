package org.example.data.datasources.project_data_source

import org.example.data.models.ProjectModel

interface IProjectDataSource {
    fun read(): Result<List<ProjectModel>>
    fun overWrite(users: List<ProjectModel>): Result<Boolean>
    fun append(users: List<ProjectModel>): Result<Boolean>
}