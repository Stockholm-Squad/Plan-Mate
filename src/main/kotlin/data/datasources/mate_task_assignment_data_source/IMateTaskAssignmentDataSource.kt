package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignment

interface IMateTaskAssignmentDataSource {
    fun read(): Result<List<MateTaskAssignment>>
    fun overWrite(users: List<MateTaskAssignment>): Result<Boolean>
    fun append(users: List<MateTaskAssignment>): Result<Boolean>
}