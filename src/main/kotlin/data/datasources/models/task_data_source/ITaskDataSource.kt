package org.example.data.datasources.models.task_data_source

import org.example.data.models.Task

interface ITaskDataSource {
    fun read(): Result<List<Task>>
    fun overWrite(users: List<Task>): Result<Boolean>
    fun append(users: List<Task>): Result<Boolean>
}