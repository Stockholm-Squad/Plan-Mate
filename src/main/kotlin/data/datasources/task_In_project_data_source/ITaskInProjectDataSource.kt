package org.example.data.datasources.task_In_project_data_source

import data.models.TaskInProject

interface ITaskInProjectDataSource {
    fun read(): Result<List<TaskInProject>>
    fun overWrite(users: List<TaskInProject>): Result<Boolean>
    fun append(users: List<TaskInProject>): Result<Boolean>
}