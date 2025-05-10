package org.example.data.csv_reader_writer.task_in_project

import data.dto.TaskInProjectDto

interface ITaskInProjectCSVReaderWriter {
    suspend fun read(): List<TaskInProjectDto>
    suspend fun overWrite(tasks: List<TaskInProjectDto>): Boolean
    suspend fun append(tasks: List<TaskInProjectDto>): Boolean
}