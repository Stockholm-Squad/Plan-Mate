package org.example.data.datasources

import data.dto.ProjectStateDto

interface IStateCSVReaderWriter {
    suspend fun read(): List<ProjectStateDto>
    suspend fun overWrite(state: List<ProjectStateDto>): Boolean
    suspend fun append(state: List<ProjectStateDto>): Boolean
}