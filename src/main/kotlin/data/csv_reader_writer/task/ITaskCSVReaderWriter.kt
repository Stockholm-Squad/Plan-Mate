package org.example.data.csv_reader_writer.task

import data.dto.TaskDto

interface ITaskCSVReaderWriter {
    suspend fun read(): List<TaskDto>
    suspend fun overWrite(tasks: List<TaskDto>): Boolean
    suspend fun append(tasks: List<TaskDto>): Boolean
}