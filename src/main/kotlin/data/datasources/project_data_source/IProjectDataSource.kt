package org.example.data.datasources.project_data_source

import org.example.data.models.ProjectModel

interface IProjectDataSource {
    suspend fun read(): List<ProjectModel>
    suspend fun overWrite(projects: List<ProjectModel>): Boolean
    suspend fun append(projects: List<ProjectModel>): Boolean
}