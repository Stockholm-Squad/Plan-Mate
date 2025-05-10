package org.example.data.datasources

import data.dto.TaskInProjectDto

interface ITaskInProjectCSVReaderWriter {
    suspend fun read(): List<TaskInProjectDto>
    suspend fun overWrite(tasks: List<TaskInProjectDto>): Boolean
    suspend fun append(tasks: List<TaskInProjectDto>): Boolean
}