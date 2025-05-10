package org.example.data.source.local.csv_reader_writer.state

import data.dto.ProjectStateDto

interface IStateCSVReaderWriter {
    suspend fun read(): List<ProjectStateDto>
    suspend fun overWrite(state: List<ProjectStateDto>): Boolean
    suspend fun append(state: List<ProjectStateDto>): Boolean
}