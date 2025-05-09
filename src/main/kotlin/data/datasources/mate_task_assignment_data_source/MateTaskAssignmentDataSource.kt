package org.example.data.datasources.mate_task_assignment_data_source

import data.models.MateTaskAssignmentModel

interface MateTaskAssignmentDataSource {
    suspend fun addUserToTask(mateName: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean
    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentModel>
    suspend fun getUsersMateTaskByUserName(taskId: String): List<MateTaskAssignmentModel>
}