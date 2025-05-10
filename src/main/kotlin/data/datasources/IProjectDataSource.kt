package org.example.data.datasources

import data.dto.ProjectModel

interface IProjectDataSource {
    suspend fun read(): List<ProjectModel>
    suspend fun overWrite(projects: List<ProjectModel>): Boolean
    suspend fun append(projects: List<ProjectModel>): Boolean
}