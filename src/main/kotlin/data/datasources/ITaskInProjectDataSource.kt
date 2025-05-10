package org.example.data.datasources

import data.dto.TaskInProjectDto

interface ITaskInProjectDataSource {
    suspend fun read(): List<TaskInProjectDto>
    suspend fun overWrite(tasks: List<TaskInProjectDto>): Boolean
    suspend fun append(tasks: List<TaskInProjectDto>): Boolean
}