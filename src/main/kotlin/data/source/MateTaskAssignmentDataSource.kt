package org.example.data.source

import data.dto.MateTaskAssignmentDto

interface MateTaskAssignmentDataSource {
    suspend fun addUserToTask(username: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(username: String, taskId: String): Boolean
    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto>
    suspend fun getUsersMateTaskByUserName(username: String): List<MateTaskAssignmentDto>
}