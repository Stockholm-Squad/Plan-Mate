package org.example.data.utils.csv_reader_writer.project

import data.dto.ProjectDto

interface IProjectCSVReaderWriter {
    suspend fun read(): List<ProjectDto>
    suspend fun overWrite(projects: List<ProjectDto>): Boolean
    suspend fun append(projects: List<ProjectDto>): Boolean
}