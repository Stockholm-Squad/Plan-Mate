package org.example.data.source

import data.dto.MateTaskAssignmentDto

interface MateTaskAssignmentDataSource {
    suspend fun addUserToTask(mateName: String, taskId: String): Boolean
    suspend fun deleteUserFromTask(mateName: String, taskId: String): Boolean
    suspend fun getUsersMateTaskByTaskId(taskId: String): List<MateTaskAssignmentDto>
    suspend fun getUsersMateTaskByUserName(userName: String): List<MateTaskAssignmentDto>
}