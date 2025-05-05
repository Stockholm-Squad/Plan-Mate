package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel

interface ITaskInProjectDataSource {
    fun read(): Result<List<TaskInProjectModel>>
    fun overWrite(users: List<TaskInProjectModel>): Result<Boolean>
    fun append(users: List<TaskInProjectModel>): Result<Boolean>
}