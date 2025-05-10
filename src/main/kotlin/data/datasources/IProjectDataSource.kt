package org.example.data.datasources

import data.dto.ProjectDto

interface IProjectDataSource {
    suspend fun read(): List<ProjectDto>
    suspend fun overWrite(projects: List<ProjectDto>): Boolean
    suspend fun append(projects: List<ProjectDto>): Boolean
}