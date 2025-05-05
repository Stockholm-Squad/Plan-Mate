package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel

interface IMateTaskAssignmentDataSource {
    suspend fun read(): List<MateTaskAssignmentModel>
    suspend fun overWrite(users: List<MateTaskAssignmentModel>): Boolean
    suspend fun append(users: List<MateTaskAssignmentModel>): Boolean
}