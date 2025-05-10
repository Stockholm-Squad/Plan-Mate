package org.example.data.datasources

import data.models.MateTaskAssignmentModel

interface IMateTaskAssignmentDataSource {
    suspend fun read(): List<MateTaskAssignmentModel>
    suspend fun overWrite(mateTasks: List<MateTaskAssignmentModel>): Boolean
    suspend fun append(mateTasks: List<MateTaskAssignmentModel>): Boolean
}