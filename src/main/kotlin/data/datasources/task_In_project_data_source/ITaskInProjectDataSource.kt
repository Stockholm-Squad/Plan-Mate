package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProjectModel

interface ITaskInProjectDataSource {
    suspend fun read(): List<TaskInProjectModel>
    suspend fun overWrite(users: List<TaskInProjectModel>): Boolean
    suspend fun append(users: List<TaskInProjectModel>): Boolean
}