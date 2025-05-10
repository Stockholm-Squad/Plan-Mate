package org.example.data.datasources

import data.dto.TaskDto

interface ITaskDataSource {
    suspend fun read(): List<TaskDto>
    suspend fun overWrite(tasks: List<TaskDto>): Boolean
    suspend fun append(tasks: List<TaskDto>): Boolean
}