package org.example.data.source.local.csv_reader_writer.state

import data.dto.EntityStateDto

interface IStateCSVReaderWriter {
    suspend fun read(): List<EntityStateDto>
    suspend fun overWrite(state: List<EntityStateDto>): Boolean
    suspend fun append(state: List<EntityStateDto>): Boolean
}