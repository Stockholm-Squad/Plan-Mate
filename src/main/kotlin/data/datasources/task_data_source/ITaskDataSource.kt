package org.example.data.datasources.task_data_source

import org.example.data.models.TaskModel

interface ITaskDataSource {
    fun read(): Result<List<TaskModel>>
    fun overWrite(users: List<TaskModel>): Result<Boolean>
    fun append(users: List<TaskModel>): Result<Boolean>
}