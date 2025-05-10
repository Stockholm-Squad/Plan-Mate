package org.example.data.datasources

import org.example.data.models.TaskModel

interface ITaskDataSource {
    suspend fun read(): List<TaskModel>
    suspend fun overWrite(tasks: List<TaskModel>): Boolean
    suspend fun append(tasks: List<TaskModel>): Boolean
}