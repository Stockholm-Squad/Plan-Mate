package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel

interface IMateTaskAssignmentDataSource {
    fun read(): Result<List<MateTaskAssignmentModel>>
    fun overWrite(users: List<MateTaskAssignmentModel>): Result<Boolean>
    fun append(users: List<MateTaskAssignmentModel>): Result<Boolean>
}