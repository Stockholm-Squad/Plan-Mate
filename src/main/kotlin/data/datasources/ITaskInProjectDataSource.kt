package org.example.data.datasources

import data.dto.TaskInProjectModel

interface ITaskInProjectDataSource {
    suspend fun read(): List<TaskInProjectModel>
    suspend fun overWrite(tasks: List<TaskInProjectModel>): Boolean
    suspend fun append(tasks: List<TaskInProjectModel>): Boolean
}